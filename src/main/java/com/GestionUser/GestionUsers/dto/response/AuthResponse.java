package com.GestionUser.GestionUsers.dto.response;
public record AuthResponse(String accessToken, String refreshToken, String email, String role, long expiresIn) {}