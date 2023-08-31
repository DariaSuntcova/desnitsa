package ru.desnitsa.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.desnitsa.backend.dto.UserCredentials;
import ru.desnitsa.backend.dto.UserDto;
import ru.desnitsa.backend.dto.UserToEdit;
import ru.desnitsa.backend.dto.UserToSave;
import ru.desnitsa.backend.services.UserService;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@RequestBody UserToSave userToSave) {
        return userService.saveUser(userToSave);
    }

    @PutMapping("{id}")
    public UserDto updateUser(@PathVariable Long id, @RequestBody UserToEdit userToEdit) {
        return userService.updateUser(id, userToEdit);
    }

    @PutMapping
    public UserDto updateUserCredentials(@RequestBody UserCredentials userCredentials) {
        return userService.updateUserCredentials(userCredentials);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id, Principal principal) {
        userService.deleteUser(id, principal);
    }
}
