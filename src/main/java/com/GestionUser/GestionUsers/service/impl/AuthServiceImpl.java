package com.app.service.impl;

import com.app.dto.request.*;
import com.app.dto.response.*;
import com.app.entity.*;
import com.app.enums.Role;
import com.app.exception.*;
import com.app.repository.*;
import com.app.security.JwtService;
import com.app.service.AuthService;
import com.app.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;

    @Value("${security.rate-limit.login-attempts}")
    private int maxLoginAttempts;

    @Value("${security.rate-limit.lockout-duration-minutes}")
    private int lockoutDurationMinutes;

    // ─── Register ────────────────────────────────────────────────────

    @Override
    public CompletableFuture<ApiResponse<AuthResponse>> register(
        RegisterRequest request,
        HttpServletRequest httpRequest
    ) {
        return CompletableFuture.supplyAsync(() -> {
            // Uniqueness checks
            if (userRepository.existsByEmail(request.email())) {
                throw new ConflictException("Email already registered");
            }
            if (userRepository.existsByUsername(request.username())) {
                throw new ConflictException("Username already taken");
            }

            User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .role(request.role() != null ? request.role() : Role.ROLE_USER)
                .passwordChangedAt(LocalDateTime.now())
                .build();

            user = userRepository.save(user);

            String accessToken  = jwtService.generateAccessToken(user);
            String refreshToken = refreshTokenService.createRefreshToken(
                user, extractDeviceInfo(httpRequest), extractIpAddress(httpRequest)
            );

            log.info("User registered: {} ({})", user.getUsername(), user.getRole());
            return ApiResponse.created(buildAuthResponse(accessToken, refreshToken, user));
        });
    }

    // ─── Login ───────────────────────────────────────────────────────

    @Override
    public CompletableFuture<ApiResponse<AuthResponse>> login(
        LoginRequest request,
        HttpServletRequest httpRequest
    ) {
        return CompletableFuture.supplyAsync(() -> {
            User user = userRepository
                .findByUsernameOrEmail(request.usernameOrEmail(), request.usernameOrEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

            // Brute-force protection
            checkAccountLock(user);

            try {
                Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        user.getUsername(), request.password()
                    )
                );

                // Reset failed attempts on success
                user.setFailedLoginAttempts(0);
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);

                String accessToken  = jwtService.generateAccessToken((User) auth.getPrincipal());
                String refreshToken = refreshTokenService.createRefreshToken(
                    user, extractDeviceInfo(httpRequest), extractIpAddress(httpRequest)
                );

                log.info("User logged in: {}", user.getUsername());
                return ApiResponse.ok("Login successful",
                    buildAuthResponse(accessToken, refreshToken, user));

            } catch (BadCredentialsException ex) {
                incrementFailedAttempts(user);
                throw new UnauthorizedException("Invalid credentials");
            }
        });
    }

    // ─── Refresh Token ────────────────────────────────────────────────

    @Override
    public CompletableFuture<ApiResponse<TokenRefreshResponse>> refreshToken(
        RefreshTokenRequest request
    ) {
        return CompletableFuture.supplyAsync(() -> {
            RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.refreshToken());
            User user = refreshToken.getUser();

            String newAccessToken = jwtService.generateAccessToken(user);
            long expiresIn = jwtService.getAccessTokenExpiration();

            return ApiResponse.ok(TokenRefreshResponse.of(newAccessToken, expiresIn));
        });
    }

    // ─── Logout ──────────────────────────────────────────────────────

    @Override
    public CompletableFuture<ApiResponse<Void>> logout(String refreshToken) {
        return CompletableFuture.supplyAsync(() -> {
            refreshTokenService.revokeToken(refreshToken);
            return ApiResponse.<Void>ok("Logged out successfully", null);
        });
    }

    // ─── Logout All Devices ──────────────────────────────────────────

    @Override
    public CompletableFuture<ApiResponse<Void>> logoutAll(String username) {
        return CompletableFuture.supplyAsync(() -> {
            userRepository.findByUsername(username)
                .ifPresent(user -> refreshTokenService.revokeAllUserTokens(user));
            return ApiResponse.<Void>ok("All sessions terminated", null);
        });
    }

    // ─── Change Password ─────────────────────────────────────────────

    @Override
    public CompletableFuture<ApiResponse<Void>> changePassword(
        String username,
        ChangePasswordRequest request
    ) {
        return CompletableFuture.supplyAsync(() -> {
            if (!request.newPassword().equals(request.confirmPassword())) {
                throw new ValidationException("Passwords do not match");
            }

            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

            if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
                throw new UnauthorizedException("Current password is incorrect");
            }

            user.setPassword(passwordEncoder.encode(request.newPassword()));
            user.setPasswordChangedAt(LocalDateTime.now());
            userRepository.save(user);

            // Revoke all refresh tokens to force re-login on other devices
            refreshTokenService.revokeAllUserTokens(user);

            return ApiResponse.<Void>ok("Password changed successfully", null);
        });
    }

    // ─── Helpers ─────────────────────────────────────────────────────

    private void checkAccountLock(User user) {
        if (!user.isAccountNonLocked()) {
            LocalDateTime unlockTime = user.getLockTime().plusMinutes(lockoutDurationMinutes);
            if (LocalDateTime.now().isBefore(unlockTime)) {
                throw new AccountLockedException("Account locked. Try again after " + unlockTime);
            }
            // Auto-unlock
            user.setAccountNonLocked(true);
            user.setFailedLoginAttempts(0);
            userRepository.save(user);
        }
    }

    private void incrementFailedAttempts(User user) {
        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);
        if (attempts >= maxLoginAttempts) {
            user.setAccountNonLocked(false);
            user.setLockTime(LocalDateTime.now());
            log.warn("Account locked after {} failed attempts: {}", attempts, user.getUsername());
        }
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(String access, String refresh, User user) {
        return AuthResponse.of(
            access, refresh,
            jwtService.getAccessTokenExpiration(),
            userMapper.toResponse(user)
        );
    }

    private String extractDeviceInfo(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    private String extractIpAddress(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return xff != null ? xff.split(",")[0].trim() : request.getRemoteAddr();
    }
}