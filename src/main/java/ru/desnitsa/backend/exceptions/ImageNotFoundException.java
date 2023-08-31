package ru.desnitsa.backend.exceptions;

public class ImageNotFoundException extends RuntimeException {

    public ImageNotFoundException() {
        super("Изображение не найдено");
    }
}
