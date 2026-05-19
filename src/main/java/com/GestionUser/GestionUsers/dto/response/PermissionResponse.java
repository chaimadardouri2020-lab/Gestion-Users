package com.GestionUser.GestionUsers.dto.response;
import java.time.LocalDateTime;
public record PermissionResponse(Long id, String name, String description, LocalDateTime createdAt) {}