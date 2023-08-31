package ru.desnitsa.backend.dto;

public record UserToEdit(
        String fullName,
        String email,
        String phoneNumber,
        String role
) {
}
