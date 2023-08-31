package ru.desnitsa.backend.entities;

import org.springframework.data.annotation.Id;

public record PhoneNumber(
        @Id
        Long id,
        Long contactsId,
        String phoneNumber
) {
}
