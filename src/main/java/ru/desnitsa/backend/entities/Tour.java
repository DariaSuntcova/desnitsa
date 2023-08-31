package ru.desnitsa.backend.entities;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Table(name = "tours")
public record Tour(
        @Id
        Long id,
        @NotNull
        String title,
        String description,
        @NotNull
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        Date tourDate,
        @CreatedDate
        Instant createdDate,
        @LastModifiedDate
        Instant lastModifiedDate,
        String mainImageUrl,
//        @MappedCollection(idColumn = "tour_id")
        @MappedCollection(idColumn = "tour_id")
        Set<ImageUrls> imageUrlList
) {
    public static Tour of(String title, String description, Date tourDate, String mainImageUrl, Set<ImageUrls> imageUrlList) {
        return new Tour(null, title, description, tourDate, null, null, mainImageUrl, imageUrlList);
    }
}
