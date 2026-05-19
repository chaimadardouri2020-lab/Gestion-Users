$utf8 = New-Object System.Text.UTF8Encoding $false
$b = "$PWD\src\main\java\com\GestionUser\GestionUsers"

@(
  "$b\enums","$b\entity","$b\dto\request","$b\dto\response",
  "$b\repository","$b\mapper","$b\exception","$b\security",
  "$b\config","$b\service\interfaces","$b\service\impl","$b\controller"
) | ForEach-Object { New-Item -ItemType Directory -Force -Path $_ | Out-Null }

Write-Host "Dossiers OK" -ForegroundColor Green

$files = @{}

$files["$b\enums\Role.java"] = 'package com.GestionUser.GestionUsers.enums;
public enum Role { ROLE_VIEWER, ROLE_USER, ROLE_MANAGER, ROLE_ADMIN, ROLE_SUPER_ADMIN }'

$files["$b\dto\request\LoginRequest.java"] = 'package com.GestionUser.GestionUsers.dto.request;
import jakarta.validation.constraints.*;
public record LoginRequest(@NotBlank @Email String email, @NotBlank @Size(min=8) String password) {}'

$files["$b\dto\request\RegisterRequest.java"] = 'package com.GestionUser.GestionUsers.dto.request;
import jakarta.validation.constraints.*;
public record RegisterRequest(@NotBlank String firstName, @NotBlank String lastName, @NotBlank @Email String email, @NotBlank String password, @NotBlank String role) {}'

$files["$b\dto\request\RefreshTokenRequest.java"] = 'package com.GestionUser.GestionUsers.dto.request;
import jakarta.validation.constraints.NotBlank;
public record RefreshTokenRequest(@NotBlank String refreshToken) {}'

$files["$b\dto\request\UpdateUserRequest.java"] = 'package com.GestionUser.GestionUsers.dto.request;
public record UpdateUserRequest(String firstName, String lastName, String role) {}'

$files["$b\dto\request\ChangePasswordRequest.java"] = 'package com.GestionUser.GestionUsers.dto.request;
import jakarta.validation.constraints.NotBlank;
public record ChangePasswordRequest(@NotBlank String currentPassword, @NotBlank String newPassword) {}'

$files["$b\dto\response\ApiResponse.java"] = 'package com.GestionUser.GestionUsers.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean success, String message, T data, String error) {
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(true,"OK",data,null); }
    public static <T> ApiResponse<T> ok(String msg, T data) { return new ApiResponse<>(true,msg,data,null); }
    public static <T> ApiResponse<T> error(String err) { return new ApiResponse<>(false,null,null,err); }
}'

$files["$b\dto\response\AuthResponse.java"] = 'package com.GestionUser.GestionUsers.dto.response;
public record AuthResponse(String accessToken, String refreshToken, String email, String role, long expiresIn) {}'

$files["$b\dto\response\TokenRefreshResponse.java"] = 'package com.GestionUser.GestionUsers.dto.response;
public record TokenRefreshResponse(String accessToken, String refreshToken, long expiresIn) {}'

$files["$b\dto\response\PagedResponse.java"] = 'package com.GestionUser.GestionUsers.dto.response;
import java.util.List;
public record PagedResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {}'

$files["$b\dto\response\UserResponse.java"] = 'package com.GestionUser.GestionUsers.dto.response;
import java.time.LocalDateTime;
public record UserResponse(Long id, String firstName, String lastName, String email, String role, boolean enabled, LocalDateTime createdAt) {}'

$files["$b\repository\UserRepository.java"] = 'package com.GestionUser.GestionUsers.repository;
import com.GestionUser.GestionUsers.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}'

$files["$b\mapper\UserMapper.java"] = 'package com.GestionUser.GestionUsers.mapper;
import com.GestionUser.GestionUsers.dto.response.UserResponse;
import com.GestionUser.GestionUsers.entity.User;
import org.springframework.stereotype.Component;
@Component
public class UserMapper {
    public UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getFirstName(), user.getLastName(),
            user.getEmail(), user.getRole().name(), user.isEnabled(), user.getCreatedAt());
    }
}'

$files["$b\exception\ResourceNotFoundException.java"] = 'package com.GestionUser.GestionUsers.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) { super(message); }
}'

$files["$b\exception\EmailAlreadyExistsException.java"] = 'package com.GestionUser.GestionUsers.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) { super("Email deja utilise: " + email); }
}'

$files["$b\exception\InvalidTokenException.java"] = 'package com.GestionUser.GestionUsers.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) { super(message); }
}'

$files["$b\exception\GlobalExceptionHandler.java"] = 'package com.GestionUser.GestionUsers.exception;
import com.GestionUser.GestionUsers.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.stream.Collectors;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Email ou mot de passe incorrect"));
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Acces refuse"));
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleEmailExists(EmailAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage()).collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(errors));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneral(Exception ex) {
        log.error("Erreur: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Erreur interne"));
    }
}'

$files["$b\entity\User.java"] = 'package com.GestionUser.GestionUsers.entity;
import com.GestionUser.GestionUsers.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder(toBuilder = true)
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String firstName;
    @Column(nullable = false) private String lastName;
    @Column(unique = true, nullable = false) private String email;
    @Column(nullable = false) private String password;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role;
    @Builder.Default private boolean enabled = true;
    @Builder.Default private boolean accountNonLocked = true;
    @Builder.Default private int failedAttempts = 0;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return enabled; }
}'

$files["$b\security\JwtService.java"] = 'package com.GestionUser.GestionUsers.security;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.*;
@Slf4j
@Service
public class JwtService {
    @Value("${jwt.secret}") private String secret;
    @Value("${jwt.expiration}") private long expiration;
    @Value("${jwt.refresh-expiration}") private long refreshExpiration;
    private SecretKey getSigningKey() { return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret)); }
    public String generateToken(UserDetails u) { return buildToken(u, expiration); }
    public String generateRefreshToken(UserDetails u) { return buildToken(u, refreshExpiration); }
    private String buildToken(UserDetails u, long expiry) {
        return Jwts.builder().id(UUID.randomUUID().toString()).subject(u.getUsername())
            .claim("roles", u.getAuthorities()).issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiry)).signWith(getSigningKey()).compact();
    }
    public String extractUsername(String token) { return getClaims(token).getSubject(); }
    public boolean isTokenValid(String token, UserDetails u) {
        try { return extractUsername(token).equals(u.getUsername()) && !getClaims(token).getExpiration().before(new Date()); }
        catch (JwtException e) { log.warn("Invalid JWT: {}", e.getMessage()); return false; }
    }
    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }
}'

$files["$b\security\JwtAuthenticationFilter.java"] = 'package com.GestionUser.GestionUsers.security;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component @RequiredArgsConstructor @Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        String bearer = request.getHeader("Authorization");
        if (!StringUtils.hasText(bearer) || !bearer.startsWith("Bearer ")) { chain.doFilter(request, response); return; }
        try {
            String jwt = bearer.substring(7);
            String username = jwtService.extractUsername(jwt);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails ud = userDetailsService.loadUserByUsername(username);
                if (jwtService.isTokenValid(jwt, ud)) {
                    var auth = new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        } catch (Exception e) { log.warn("JWT error: {}", e.getMessage()); }
        chain.doFilter(request, response);
    }
    @Override protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/v1/auth/");
    }
}'

$files["$b\security\SecurityConfig.java"] = 'package com.GestionUser.GestionUsers.security;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;
@Configuration @EnableWebSecurity @EnableMethodSecurity @RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    @Bean public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**","/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPER_ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/api/v1/users/**").hasAnyAuthority("ROLE_ADMIN","ROLE_SUPER_ADMIN")
                .anyRequest().authenticated())
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class).build();
    }
    @Bean public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of("http://localhost:4200"));
        c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        c.setAllowedHeaders(List.of("*")); c.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource s = new UrlBasedCorsConfigurationSource();
        s.registerCorsConfiguration("/**", c); return s;
    }
    @Bean public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService); p.setPasswordEncoder(passwordEncoder()); return p;
    }
    @Bean public AuthenticationManager authenticationManager(AuthenticationConfiguration c) throws Exception { return c.getAuthenticationManager(); }
    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }
}'

$files["$b\config\ApplicationConfig.java"] = 'package com.GestionUser.GestionUsers.config;
import com.GestionUser.GestionUsers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
@Configuration @RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;
    @Bean public UserDetailsService userDetailsService() {
        return username -> userRepository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}'

$files["$b\service\interfaces\IAuthService.java"] = 'package com.GestionUser.GestionUsers.service.interfaces;
import com.GestionUser.GestionUsers.dto.request.LoginRequest;
import com.GestionUser.GestionUsers.dto.request.RefreshTokenRequest;
import com.GestionUser.GestionUsers.dto.request.RegisterRequest;
import com.GestionUser.GestionUsers.dto.response.AuthResponse;
import com.GestionUser.GestionUsers.dto.response.TokenRefreshResponse;
public interface IAuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    TokenRefreshResponse refreshToken(RefreshTokenRequest request);
    void logout(String token);
}'

$files["$b\service\interfaces\IUserService.java"] = 'package com.GestionUser.GestionUsers.service.interfaces;
import com.GestionUser.GestionUsers.dto.request.UpdateUserRequest;
import com.GestionUser.GestionUsers.dto.response.PagedResponse;
import com.GestionUser.GestionUsers.dto.response.UserResponse;
public interface IUserService {
    PagedResponse<UserResponse> getAllUsers(int page, int size);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UpdateUserRequest request);
    void deleteUser(Long id);
}'

$files["$b\service\impl\AuthServiceImpl.java"] = 'package com.GestionUser.GestionUsers.service.impl;
import com.GestionUser.GestionUsers.dto.request.LoginRequest;
import com.GestionUser.GestionUsers.dto.request.RefreshTokenRequest;
import com.GestionUser.GestionUsers.dto.request.RegisterRequest;
import com.GestionUser.GestionUsers.dto.response.AuthResponse;
import com.GestionUser.GestionUsers.dto.response.TokenRefreshResponse;
import com.GestionUser.GestionUsers.entity.User;
import com.GestionUser.GestionUsers.enums.Role;
import com.GestionUser.GestionUsers.exception.EmailAlreadyExistsException;
import com.GestionUser.GestionUsers.exception.InvalidTokenException;
import com.GestionUser.GestionUsers.repository.UserRepository;
import com.GestionUser.GestionUsers.security.JwtService;
import com.GestionUser.GestionUsers.service.interfaces.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j @Service @RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) throw new EmailAlreadyExistsException(req.email());
        String roleName = req.role().toUpperCase().startsWith("ROLE_") ? req.role().toUpperCase() : "ROLE_" + req.role().toUpperCase();
        var user = User.builder().firstName(req.firstName()).lastName(req.lastName())
            .email(req.email()).password(passwordEncoder.encode(req.password()))
            .role(Role.valueOf(roleName)).build();
        userRepository.save(user);
        return buildAuthResponse(user);
    }
    @Override @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        var user = userRepository.findByEmail(req.email()).orElseThrow(() -> new InvalidTokenException("User not found"));
        return buildAuthResponse(user);
    }
    @Override
    public TokenRefreshResponse refreshToken(RefreshTokenRequest req) {
        String username = jwtService.extractUsername(req.refreshToken());
        var user = userRepository.findByEmail(username).orElseThrow(() -> new InvalidTokenException("Token invalide"));
        if (!jwtService.isTokenValid(req.refreshToken(), user)) throw new InvalidTokenException("Token expire");
        return new TokenRefreshResponse(jwtService.generateToken(user), req.refreshToken(), 900000L);
    }
    @Override public void logout(String token) { log.info("Logout effectue"); }
    private AuthResponse buildAuthResponse(User user) {
        return new AuthResponse(jwtService.generateToken(user), jwtService.generateRefreshToken(user),
            user.getEmail(), user.getRole().name(), 900000L);
    }
}'

$files["$b\service\impl\UserServiceImpl.java"] = 'package com.GestionUser.GestionUsers.service.impl;
import com.GestionUser.GestionUsers.dto.request.UpdateUserRequest;
import com.GestionUser.GestionUsers.dto.response.PagedResponse;
import com.GestionUser.GestionUsers.dto.response.UserResponse;
import com.GestionUser.GestionUsers.entity.User;
import com.GestionUser.GestionUsers.enums.Role;
import com.GestionUser.GestionUsers.exception.ResourceNotFoundException;
import com.GestionUser.GestionUsers.mapper.UserMapper;
import com.GestionUser.GestionUsers.repository.UserRepository;
import com.GestionUser.GestionUsers.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Slf4j @Service @RequiredArgsConstructor
public class UserServiceImpl implements IUserService, UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Not found: " + email));
    }
    @Override @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(int page, int size) {
        var p = userRepository.findAll(PageRequest.of(page, size));
        return new PagedResponse<>(p.getContent().stream().map(userMapper::toResponse).toList(),
            p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }
    @Override @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return userMapper.toResponse(userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id)));
    }
    @Override @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest req) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        if (req.firstName() != null) user.setFirstName(req.firstName());
        if (req.lastName() != null) user.setLastName(req.lastName());
        if (req.role() != null) user.setRole(Role.valueOf(req.role().toUpperCase()));
        return userMapper.toResponse(userRepository.save(user));
    }
    @Override @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) throw new ResourceNotFoundException("User not found: " + id);
        userRepository.deleteById(id);
    }
}'

$files["$b\controller\AuthController.java"] = 'package com.GestionUser.GestionUsers.controller;
import com.GestionUser.GestionUsers.dto.request.LoginRequest;
import com.GestionUser.GestionUsers.dto.request.RefreshTokenRequest;
import com.GestionUser.GestionUsers.dto.request.RegisterRequest;
import com.GestionUser.GestionUsers.dto.response.ApiResponse;
import com.GestionUser.GestionUsers.service.interfaces.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;
@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    @PostMapping("/register")
    public CompletableFuture<ResponseEntity<ApiResponse<?>>> register(@Valid @RequestBody RegisterRequest req) {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(ApiResponse.ok("Inscription reussie", authService.register(req))));
    }
    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<ApiResponse<?>>> login(@Valid @RequestBody LoginRequest req) {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(ApiResponse.ok("Connexion reussie", authService.login(req))));
    }
    @PostMapping("/refresh")
    public CompletableFuture<ResponseEntity<ApiResponse<?>>> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(ApiResponse.ok("Token renouvele", authService.refreshToken(req))));
    }
    @PostMapping("/logout")
    public CompletableFuture<ResponseEntity<ApiResponse<?>>> logout(@RequestHeader("Authorization") String authHeader) {
        return CompletableFuture.supplyAsync(() -> { authService.logout(authHeader.substring(7));
            return ResponseEntity.ok(ApiResponse.ok("Deconnexion reussie", null)); });
    }
}'

$files["$b\controller\UserController.java"] = 'package com.GestionUser.GestionUsers.controller;
import com.GestionUser.GestionUsers.dto.request.UpdateUserRequest;
import com.GestionUser.GestionUsers.dto.response.ApiResponse;
import com.GestionUser.GestionUsers.service.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;
@RestController @RequestMapping("/api/v1/users") @RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    @GetMapping
    @PreAuthorize("hasAnyAuthority('"'"'ROLE_ADMIN'"'"','"'"'ROLE_SUPER_ADMIN'"'"')")
    public CompletableFuture<ResponseEntity<ApiResponse<?>>> getAllUsers(
        @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(ApiResponse.ok(userService.getAllUsers(page, size))));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('"'"'ROLE_ADMIN'"'"','"'"'ROLE_SUPER_ADMIN'"'"','"'"'ROLE_MANAGER'"'"')")
    public CompletableFuture<ResponseEntity<ApiResponse<?>>> getUser(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(ApiResponse.ok(userService.getUserById(id))));
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('"'"'ROLE_ADMIN'"'"','"'"'ROLE_SUPER_ADMIN'"'"')")
    public CompletableFuture<ResponseEntity<ApiResponse<?>>> updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest req) {
        return CompletableFuture.supplyAsync(() -> ResponseEntity.ok(ApiResponse.ok("Mis a jour", userService.updateUser(id, req))));
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('"'"'ROLE_ADMIN'"'"','"'"'ROLE_SUPER_ADMIN'"'"')")
    public CompletableFuture<ResponseEntity<ApiResponse<?>>> deleteUser(@PathVariable Long id) {
        return CompletableFuture.supplyAsync(() -> { userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.ok("Supprime", null)); });
    }
}'

foreach ($path in $files.Keys) {
    $dir = Split-Path $path
    if (!(Test-Path $dir)) { New-Item -ItemType Directory -Force -Path $dir | Out-Null }
    [System.IO.File]::WriteAllText($path, $files[$path], $utf8)
    Write-Host "Created: $(Split-Path $path -Leaf)" -ForegroundColor Cyan
}

Write-Host "`nTOUS LES FICHIERS CREES SANS BOM" -ForegroundColor Green
