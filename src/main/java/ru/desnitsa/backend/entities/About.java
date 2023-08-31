package ru.desnitsa.backend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("about")
public record About(
        @Id
        Long id,
        String constant,
        String description,
        String firstImageUrl,
        String secondImageUrl
) {
    public static About of(String description, String firstImageUrl, String secondImageUrl) {
        return new About(null, "about", description, firstImageUrl, secondImageUrl);
    }
}
