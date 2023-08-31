package ru.desnitsa.backend.dto;

import org.springframework.web.multipart.MultipartFile;

public record AboutDto(
        Long id,
        String description,
        MultipartFile firstImageFile,
        MultipartFile secondImageFile
) {
}
