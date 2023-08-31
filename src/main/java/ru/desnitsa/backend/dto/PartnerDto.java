package ru.desnitsa.backend.dto;

import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

public record PartnerDto(
        @NotNull
        String title,
        String link,
        MultipartFile imageFile
) {
}
