package com.GestionUser.GestionUsers.dto.response;
import java.time.LocalDateTime;
import java.util.Set;
public record UserResponse(Long id, String firstName, String lastName, String email, Set<String> roles, boolean enabled, LocalDateTime createdAt) {}