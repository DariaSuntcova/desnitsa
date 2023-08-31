package ru.desnitsa.backend.dto;

import ru.desnitsa.backend.entities.Role;
import ru.desnitsa.backend.entities.User;

import java.util.Set;

public record UserDto(
        Long id,
        String username,
        String fullName,
        String email,
        String phoneNumber,
        Set<Role> roles
) {
    public static UserDto of(User user) {
        return new UserDto(user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRoles());
    }
}
