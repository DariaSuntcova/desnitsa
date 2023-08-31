package ru.desnitsa.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.desnitsa.backend.exceptions.*;

@RestControllerAdvice
public class ControllersAdvice {

    @ExceptionHandler(UserAlreadyExistException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String userAlreadyExistException(UserAlreadyExistException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String userNotFoundException(UserNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UserPasswordMismatchException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String userPasswordMismatchException(UserPasswordMismatchException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    String badCredentialsException(BadCredentialsException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UnableToDeleteUserException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    String unableToDeleteUserException(UnableToDeleteUserException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(IOBadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String ioBadRequestException(IOBadRequestException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ContentNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    String contentNotFoundExceptionHandler(ContentNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UnsupportedMediaTypeException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    String unsupportedMediaTypeStatusException(UnsupportedMediaTypeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(MemberNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String memberNotFoundExceptionHandler(MemberNotFoundException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String badRequestExceptionHandler(BadRequestException ex) {
        return ex.getMessage();
    }


}
