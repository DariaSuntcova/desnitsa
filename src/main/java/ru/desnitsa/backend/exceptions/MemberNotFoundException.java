package ru.desnitsa.backend.exceptions;

public class MemberNotFoundException extends RuntimeException{
    public MemberNotFoundException() {
        super("Член команды не найден");
    }
}
