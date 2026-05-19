package com.GestionUser.GestionUsers.dto.response;
import java.time.LocalDateTime;
import java.util.Set;
public record RoleResponse(Long id, String name, String description, Set<PermissionResponse> permissions, LocalDateTime createdAt) {}