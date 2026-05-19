package com.GestionUser.GestionUsers.dto.response;
public record TokenRefreshResponse(String accessToken, String refreshToken, long expiresIn) {}