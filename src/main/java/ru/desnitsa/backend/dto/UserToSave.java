package ru.desnitsa.backend.dto;

import javax.validation.constraints.NotNull;

public record UserToSave(
        @NotNull(message = "Необходимо указать имя пользователя.")
        String username,
        @NotNull(message = "Необходимо указать пароль пользователя.")
        String password,
        @NotNull(message = "Необходимо подтвердить пароль пользователя.")
        String confirmPassword,
        String fullName,
        String email,
        String phoneNumber,
        @NotNull(message = "Необходимо указать должность.")
        String role
) {
}
