package ru.desnitsa.backend.exceptions;

public class UserPasswordMismatchException extends RuntimeException {

    public UserPasswordMismatchException() {
        super("Пароли не совпадают.");
    }
}
