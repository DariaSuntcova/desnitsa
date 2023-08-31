package ru.desnitsa.backend.dto;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public record NewsUpdateDto(
        @NotNull
        String title,
        @NotNull
        String description,
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        Date newsDate,
        String videoLink,
        MultipartFile mainImageFile,
        List<String> imagesToDelete,
        List<MultipartFile> imagesToAdd
) {

}
