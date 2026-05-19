package com.GestionUser.GestionUsers.dto.request;
import jakarta.validation.constraints.NotBlank;
public record CreatePermissionRequest(@NotBlank String name, String description) {}