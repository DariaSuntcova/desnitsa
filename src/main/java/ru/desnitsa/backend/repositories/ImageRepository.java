package ru.desnitsa.backend.repositories;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.desnitsa.backend.entities.Image;

import java.util.Optional;

public interface ImageRepository extends CrudRepository<Image, Long> {

    Optional<Image> findImageByImageName(String imageName);
//    void deleteByImageName(String imageName);
    @Modifying
    @Query("delete from images where image_name = :imageName")
    void deleteByImageName(String imageName);
}
