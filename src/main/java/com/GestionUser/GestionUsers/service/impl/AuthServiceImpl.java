package com.GestionUser.GestionUsers.service.impl;
import com.GestionUser.GestionUsers.dto.request.*;
import com.GestionUser.GestionUsers.dto.response.*;
import com.GestionUser.GestionUsers.entity.Role;
import com.GestionUser.GestionUsers.entity.User;
import com.GestionUser.GestionUsers.exception.EmailAlreadyExistsException;
import com.GestionUser.GestionUsers.exception.InvalidTokenException;
import com.GestionUser.GestionUsers.repository.RoleRepository;
import com.GestionUser.GestionUsers.repository.UserRepository;
import com.GestionUser.GestionUsers.security.JwtService;
import com.GestionUser.GestionUsers.service.interfaces.IAuthService;
import com.GestionUser.GestionUsers.service.interfaces.IHistoriqueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.Set;
@Service @RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final IHistoriqueService historiqueService;
    @Override @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) throw new EmailAlreadyExistsException(req.email());
        String roleName = req.role().toUpperCase().startsWith("ROLE_") ? req.role().toUpperCase() : "ROLE_" + req.role().toUpperCase();
        Role role = roleRepository.findByName(roleName).orElseGet(() -> {
            Role r = new Role(roleName, "Auto-created role");
            return roleRepository.save(r);
        });
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        User user = User.builder().firstName(req.firstName()).lastName(req.lastName())
            .email(req.email()).password(passwordEncoder.encode(req.password()))
            .roles(roles).build();
        userRepository.save(user);
        historiqueService.log(user, "REGISTER", "Nouveau compte cree", null, "User", user.getId());
        log.info("Registered: {}", user.getEmail());
        return buildAuthResponse(user);
    }
    @Override @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        User user = userRepository.findByEmail(req.email()).orElseThrow(() -> new InvalidTokenException("User not found"));
        historiqueService.log(user, "LOGIN", "Connexion reussie", null, "User", user.getId());
        return buildAuthResponse(user);
    }
    @Override
    public TokenRefreshResponse refreshToken(RefreshTokenRequest req) {
        String username = jwtService.extractUsername(req.refreshToken());
        User user = userRepository.findByEmail(username).orElseThrow(() -> new InvalidTokenException("Token invalide"));
        if (!jwtService.isTokenValid(req.refreshToken(), user)) throw new InvalidTokenException("Token expire");
        return new TokenRefreshResponse(jwtService.generateToken(user), req.refreshToken(), 3600000L);
    }
    @Override public void logout(String token) { log.info("Logout effectue"); }
    private AuthResponse buildAuthResponse(User user) {
        String role = user.getRoles().stream().findFirst().map(Role::getName).orElse("ROLE_USER");
        return new AuthResponse(jwtService.generateToken(user), jwtService.generateRefreshToken(user), user.getEmail(), role, 3600000L);
    }
}