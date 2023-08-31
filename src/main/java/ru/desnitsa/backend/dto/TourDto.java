package ru.desnitsa.backend.dto;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

import java.util.Date;
import java.util.List;

public record TourDto(
        @NotNull
        String title,
        String description,
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        Date tourDate,
        MultipartFile mainImageFile,
        List<MultipartFile> imageList
) {
}
