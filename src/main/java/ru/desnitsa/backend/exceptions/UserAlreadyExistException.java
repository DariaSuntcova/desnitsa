package ru.desnitsa.backend.exceptions;

public class UserAlreadyExistException extends RuntimeException {

    public UserAlreadyExistException(String username) {
        super("Пользователь с именем " + username + " уже существует.");
    }
}
