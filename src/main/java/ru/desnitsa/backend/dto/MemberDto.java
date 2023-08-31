package ru.desnitsa.backend.dto;

import org.springframework.web.multipart.MultipartFile;

public record MemberDto(
        String firstName,
        String lastName,
        String speciality,
        String profession,
        String description,
        MultipartFile imageFile
) {
}
