package ru.desnitsa.backend.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder encoder;
    @Mock
    Principal principal;
    @InjectMocks
    UserService userService;

    @Test
    void whenGetAllUserThenReturn() {
        var user = User.of("user",
                "password",
                "Artem",
                "artem@mail.ru",
                "8-955",
                Set.of(Role.of("ROLE_admin")));
        List<User> userList = List.of(user);
        var userDto = UserDto.of(user);
        List<UserDto> userDtoList = List.of(userDto);

        when(userRepository.findAll()).thenReturn(userList);

        assertThat(userService.getAllUsers()).isNotNull();
        assertThat(userService.getAllUsers()).isEqualTo(userDtoList);
    }

    @Test
    void whenGetUserByIdExistsThenReturn() {
        var user = new User(1L,
                "user",
                "password",
                "Artem",
                "artem@mail.ru",
                "8-955",
                Set.of(Role.of("ROLE_admin")));
        var userDto = UserDto.of(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThat(userService.getUserById(1L)).isNotNull();
        assertThat(userService.getUserById(1L)).isEqualTo(userDto);
    }

    @Test
    void whenGetUserByIdNotExistsThenThrow() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void whenSaveUserIsOkThenReturn() {
        var userToSave = new UserToSave("user",
                "password",
                "password",
                "Artem",
                "artem@mail.ru",
                "8-955",
                "ROLE_manager");
        var user = new User(1L,
                "user",
                "password",
                "Artem",
                "artem@mail.ru",
                "8-955",
                Set.of(Role.of("ROLE_manager")));
        var userDto = UserDto.of(user);

        when(encoder.encode(any())).thenReturn("password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThat(userService.saveUser(userToSave)).isNotNull();
        assertThat(userService.saveUser(userToSave)).isEqualTo(userDto);
    }
    @Test
    void whenSaveExistingUserThenThrow() {
        var userToSave = new UserToSave("user",
                "password",
                "password",
                "Artem",
                "artem@mail.ru",
                "8-955",
                "ROLE_manager");
        var user = new User(1L,
                "user",
                "password",
                "Artem",
                "artem@mail.ru",
                "8-955",
                Set.of(Role.of("ROLE_manager")));

        when(userRepository.findByUsername(userToSave.username())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.saveUser(userToSave))
                .isInstanceOf(UserAlreadyExistException.class)
                .hasMessage("Пользователь с именем " + userToSave.username() + " уже существует.");
    }

    @Test
    void whenSaveUserMismatchPasswordsThenThrow() {
        var userToSave = new UserToSave("user",
                "password",
                "pass",
                "Artem",
                "artem@mail.ru",
                "8-955",
                "ROLE_manager");

        when(userRepository.findByUsername(userToSave.username())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.saveUser(userToSave))
                .isInstanceOf(UserPasswordMismatchException.class)
                .hasMessage("Пароли не совпадают.");
    }

    @Test
    void whenSaveUserAdminThenAddManagerAndReturn() {
        var userToSave = new UserToSave("user",
                "password",
                "password",
                "Artem",
                "artem@mail.ru",
                "8-955",
                "ROLE_admin");
        var user = new User(1L,
                "user",
                "password",
                "Artem",
                "artem@mail.ru",
                "8-955",
                Set.of(Role.of("ROLE_admin"),Role.of("ROLE_manager")));
        var userDto = UserDto.of(user);

        when(encoder.encode(any())).thenReturn("password");
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThat(userService.saveUser(userToSave)).isNotNull();
        assertThat(userService.saveUser(userToSave)).isEqualTo(userDto);
        assertThat(userService.saveUser(userToSave).roles()).isEqualTo(userDto.roles());
    }

    @Test
    void whenUpdateUserFoundThenReturn() {
        var userToEdit = new UserToEdit("Artem", "artem@mail.ru", "8-955", "ROLE_manager");
        var user = new User(1L,
                "user",
                "password",
                "Artem",
                "artem@mail.ru",
                "8-955",
                Set.of(Role.of("ROLE_manager")));
        var userDto = UserDto.of(user);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        assertThat(userService.updateUser(1L, userToEdit)).isNotNull();
        assertThat(userService.updateUser(1L, userToEdit)).isEqualTo(userDto);
    }

    @Test
    void whenUpdateUserNotFoundThenThrow() {
        var userToEdit = new UserToEdit("Artem", "artem@mail.ru", "8-955", "ROLE_manager");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(1L, userToEdit))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("Пользователь не найден");
    }

    @Test
    void thenUpdateUserCredentialIsOkThenReturn() {
        UserCredentials userCredentials = new UserCredentials("Artem",
                "password",
                "pass",
                "pass");
        var user = new User(1L,
                "user",
                encoder.encode("password"),
                "Artem",
                "artem@mail.ru",
                "8-955",
                Set.of(Role.of("ROLE_manager")));
        var userDto = UserDto.of(user);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(encoder.matches(userCredentials.oldPassword(), user.getPassword())).thenReturn(true);
        when(userRepository.save(any())).thenReturn(user);

        assertThat(userService.updateUserCredentials(userCredentials)).isNotNull();
        assertThat(userService.updateUserCredentials(userCredentials)).isEqualTo(userDto);
    }
    @Test
    void thenUpdateUserCredentialWrongPasswordThenThrow() {
        UserCredentials userCredentials = new UserCredentials("Artem",
                "password",
                "pass",
                "pass");
        var user = new User(1L,
                "user",
                encoder.encode("password"),
                "Artem",
                "artem@mail.ru",
                "8-955",
                Set.of(Role.of("ROLE_manager")));

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(encoder.matches(userCredentials.oldPassword(), user.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.updateUserCredentials(userCredentials))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Неверный пароль пользователя.");
    }

    @Test
    void thenUpdateUserCredentialPasswordMismatchThenThrow() {
        UserCredentials userCredentials = new UserCredentials("Artem",
                "password",
                "pass",
                "passw");
        var user = new User(1L,
                "user",
                encoder.encode("password"),
                "Artem",
                "artem@mail.ru",
                "8-955",
                Set.of(Role.of("ROLE_manager")));

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(encoder.matches(userCredentials.oldPassword(), user.getPassword())).thenReturn(true);

        assertThatThrownBy(() -> userService.updateUserCredentials(userCredentials))
                .isInstanceOf(UserPasswordMismatchException.class)
                .hasMessage("Пароли не совпадают.");
    }

    @Test
    void thenDeleteCurrentAuthenticatedUserThenThrow() {
        var user = new User(1L,
                "user",
                encoder.encode("password"),
                "Artem",
                "artem@mail.ru",
                "8-955",
                Set.of(Role.of("ROLE_manager")));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(principal.getName()).thenReturn("user");

        assertThatThrownBy(() -> userService.deleteUser(1L, principal))
                .isInstanceOf(UnableToDeleteUserException.class)
                .hasMessage("Невозможно удалить текущего пользователя.");
    }

}
