package com.GestionUser.GestionUsers.dto.request;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
public record CreateRoleRequest(@NotBlank String name, String description, Set<Long> permissionIds) {}