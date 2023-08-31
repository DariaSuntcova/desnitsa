package ru.desnitsa.backend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("images")
public record Image(
        @Id
        Long id,
        String imageName,
        String type,
        Long size,
        byte[] content
) {
}
