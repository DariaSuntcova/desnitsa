package ru.desnitsa.backend.dto;

import ru.desnitsa.backend.entities.PhoneNumber;

import java.util.Set;

public record ContactsDto(
        Set<PhoneNumber> phoneNumber,
        String address,
        String email,
        String telegram,
        String vkLink,
        String youtubeLink,
        String rutubeLink
) {
}
