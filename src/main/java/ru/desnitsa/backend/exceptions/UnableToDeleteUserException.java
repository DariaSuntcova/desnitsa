package ru.desnitsa.backend.exceptions;

public class UnableToDeleteUserException extends RuntimeException {

    public UnableToDeleteUserException() {
        super("Невозможно удалить текущего пользователя.");
    }
}
