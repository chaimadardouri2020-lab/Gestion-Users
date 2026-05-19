package com.GestionUser.GestionUsers.controller;
import com.GestionUser.GestionUsers.dto.request.*;
import com.GestionUser.GestionUsers.dto.response.ApiResponse;
import com.GestionUser.GestionUsers.service.interfaces.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Inscription reussie", authService.register(req)));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Connexion reussie", authService.login(req)));
    }
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        return ResponseEntity.ok(ApiResponse.ok("Token renouvele", authService.refreshToken(req)));
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestHeader("Authorization") String authHeader) {
        authService.logout(authHeader.substring(7));
        return ResponseEntity.ok(ApiResponse.ok("Deconnexion reussie", null));
    }
}