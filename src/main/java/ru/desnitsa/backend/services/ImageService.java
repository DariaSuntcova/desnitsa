package ru.desnitsa.backend.services;

import org.springframework.stereotype.Service;

import ru.desnitsa.backend.entities.Image;
import ru.desnitsa.backend.exceptions.ImageNotFoundException;
import ru.desnitsa.backend.repositories.ImageRepository;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public Image getImage(String imageName) {
        return imageRepository.findImageByImageName(imageName).orElseThrow(ImageNotFoundException::new);
    }

    public void deleteImage(String imageName) {
        imageRepository.deleteByImageName(imageName);
    }
}
