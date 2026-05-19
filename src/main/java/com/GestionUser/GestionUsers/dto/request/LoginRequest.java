package com.GestionUser.GestionUsers.dto.request;
import jakarta.validation.constraints.*;
public record LoginRequest(@NotBlank @Email String email, @NotBlank @Size(min=8) String password) {}