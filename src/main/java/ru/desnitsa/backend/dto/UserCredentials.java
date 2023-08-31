package ru.desnitsa.backend.dto;

import javax.validation.constraints.NotNull;

public record UserCredentials(
        @NotNull
        String username,
        @NotNull
        String oldPassword,
        @NotNull
        String newPassword,
        @NotNull
        String confirmPassword
) {
}
