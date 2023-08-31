package ru.desnitsa.backend.exceptions;

public class IOBadRequestException extends RuntimeException {
    public IOBadRequestException() {
        super("Ошибка чтения/записи.");
    }
}
