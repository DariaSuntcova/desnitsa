package ru.desnitsa.backend.entities;

import org.springframework.data.annotation.Id;

public record ImageUrls(
        @Id
        Long id,
        Long tourId,
        Long newsId,
        String imageUrl
) {
}
