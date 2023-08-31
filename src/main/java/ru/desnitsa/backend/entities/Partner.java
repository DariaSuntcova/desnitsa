package ru.desnitsa.backend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Table("partners")
public record Partner(
        @Id
        @NotNull
        Long id,
        @NotNull
        String title,
        String imageUrl,
        String link
) {
    public static Partner of(String title, String imageUrl, String link) {
        return new Partner(null, title, imageUrl, link);
    }
}
