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

@Table(name = "news")
public record News(
        @Id
        Long id,
        @NotNull
        String title,
        String description,
        @NotNull
        @DateTimeFormat(pattern = "dd.MM.yyyy")
        Date newsDate,
        @CreatedDate
        Instant createdDate,
        @LastModifiedDate
        Instant lastModifiedDate,
        String videoLink,
        String mainImageUrl,
        @MappedCollection(idColumn = "news_id")
        Set<ImageUrls> imageUrlList
) {
    public static News of(String title, String description, Date newsDate, String videoLink,
                          String mainImageUrl, Set<ImageUrls> imageUrlList) {
        return new News(null, title, description, newsDate, null, null,
                videoLink, mainImageUrl, imageUrlList);
    }
}
