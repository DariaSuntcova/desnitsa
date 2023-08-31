package ru.desnitsa.backend.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Пользователь не найден");
    }
}
