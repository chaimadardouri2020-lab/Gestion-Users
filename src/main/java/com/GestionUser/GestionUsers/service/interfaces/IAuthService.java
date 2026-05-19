package com.GestionUser.GestionUsers.service.interfaces;
import com.GestionUser.GestionUsers.dto.request.LoginRequest;
import com.GestionUser.GestionUsers.dto.request.RefreshTokenRequest;
import com.GestionUser.GestionUsers.dto.request.RegisterRequest;
import com.GestionUser.GestionUsers.dto.response.AuthResponse;
import com.GestionUser.GestionUsers.dto.response.TokenRefreshResponse;
public interface IAuthService {
    AuthResponse register(RegisterRequest req);
    AuthResponse login(LoginRequest req);
    TokenRefreshResponse refreshToken(RefreshTokenRequest req);
    void logout(String token);
}