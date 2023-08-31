package ru.desnitsa.backend.exceptions;

public class UnsupportedMediaTypeException extends RuntimeException {
    public UnsupportedMediaTypeException() {
        super("Один или несколько файлов не являются изображением gif, jpg, png, bmp");
    }
}
