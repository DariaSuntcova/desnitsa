package ru.desnitsa.backend.exceptions;

public class ContentNotFoundException extends RuntimeException {
    public ContentNotFoundException() {
        super("Контент не найден.");
    }
}
