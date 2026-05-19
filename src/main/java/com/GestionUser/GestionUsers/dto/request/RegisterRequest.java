package com.GestionUser.GestionUsers.dto.request;
import jakarta.validation.constraints.*;
public record RegisterRequest(@NotBlank String firstName, @NotBlank String lastName, @NotBlank @Email String email, @NotBlank String password, @NotBlank String role) {}