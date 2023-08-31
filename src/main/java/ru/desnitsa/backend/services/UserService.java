package ru.desnitsa.backend.services;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.desnitsa.backend.dto.UserCredentials;
import ru.desnitsa.backend.dto.UserDto;
import ru.desnitsa.backend.dto.UserToEdit;
import ru.desnitsa.backend.dto.UserToSave;
import ru.desnitsa.backend.entities.Role;
import ru.desnitsa.backend.entities.User;
import ru.desnitsa.backend.exceptions.UnableToDeleteUserException;
import ru.desnitsa.backend.exceptions.UserAlreadyExistException;
import ru.desnitsa.backend.exceptions.UserNotFoundException;
import ru.desnitsa.backend.exceptions.UserPasswordMismatchException;
import ru.desnitsa.backend.repositories.UserRepository;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private static final String ADMIN = "ROLE_admin";
    private static final String MANAGER = "ROLE_manager";
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.encoder = passwordEncoder;
    }

    public List<UserDto> getAllUsers() {
        Iterable<User> users = userRepository.findAll();
        List<UserDto> userlist = new ArrayList<>();
        for (User user : users) {
            userlist.add(new UserDto(user.getId(),
                    user.getUsername(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getPhoneNumber(),
                    user.getRoles()));
        }
        return userlist;
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return UserDto.of(user);
    }

    public UserDto saveUser(UserToSave userToSave) {
        Optional<User> userFromDb = userRepository.findByUsername(userToSave.username());
        if (!userFromDb.isEmpty()) {
            throw new UserAlreadyExistException(userToSave.username());
        }
        if (!userToSave.password().equals(userToSave.confirmPassword())) {
            throw new UserPasswordMismatchException();
        }
        User user = User.of(userToSave.username(),
                encoder.encode(userToSave.password()),
                userToSave.fullName(),
                userToSave.email(),
                userToSave.phoneNumber(),
                Set.of(Role.of(userToSave.role()))
        );
        if (userToSave.role().contains(ADMIN)) {
            user.setRoles(Set.of(Role.of(ADMIN), Role.of(MANAGER)));
        }
        return UserDto.of(userRepository.save(user));
    }

    public UserDto updateUser(Long id, UserToEdit userToEdit) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        if (userToEdit.role().contains(ADMIN)) {
            user.setRoles(Set.of(Role.of(ADMIN), Role.of(MANAGER)));
        }
        user.setFullName(userToEdit.fullName());
        user.setEmail(userToEdit.email());
        user.setPhoneNumber(userToEdit.phoneNumber());
        return UserDto.of(userRepository.save(user));
    }

    public UserDto updateUserCredentials(UserCredentials userCredentials) {
        User user = userRepository.findByUsername(userCredentials.username()).orElseThrow(UserNotFoundException::new);
        if (!encoder.matches(userCredentials.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Неверный пароль пользователя.");
        }
        if (!userCredentials.newPassword().equals(userCredentials.confirmPassword())) {
            throw new UserPasswordMismatchException();
        }
        user.setPassword(encoder.encode(userCredentials.newPassword()));
        return UserDto.of(userRepository.save(user));
    }

    public void deleteUser(Long id, Principal principal) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        String userToDeleteUsername = user.getUsername();
        String authenticatedUsername = principal.getName();
        if (userToDeleteUsername.equals(authenticatedUsername)) {
            throw new UnableToDeleteUserException();
        }
        userRepository.deleteById(id);
    }
}
