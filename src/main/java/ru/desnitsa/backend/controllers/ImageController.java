package ru.desnitsa.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.desnitsa.backend.entities.Image;
import ru.desnitsa.backend.services.ImageService;

@RestController
@RequestMapping("/image")
public class ImageController {
    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("{imageName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String imageName) {
        Image image = imageService.getImage(imageName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.type()))
                .body(image.content());
    }

    @DeleteMapping("{imageName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable String imageName) {
        imageService.deleteImage(imageName);
    }
}
