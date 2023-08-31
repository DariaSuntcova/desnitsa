package ru.desnitsa.backend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.validation.constraints.NotNull;

@Table(name = "members")
public record Member(
        @Id
        Long id,
        @NotNull
        String firstName,
        @NotNull
        String lastName,
        @NotNull
        String speciality,
        String profession,
        String description,
        String imageUrl
) {
    public static Member of(String firstName, String lastName, String speciality, String profession,
                            String description, String imageUrl) {
        return new Member(null, firstName, lastName, speciality, profession, description, imageUrl);
    }
}
