package ru.desnitsa.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.desnitsa.backend.entities.Role;
import ru.desnitsa.backend.entities.User;
import ru.desnitsa.backend.repositories.UserRepository;

import java.util.Set;

@SpringBootApplication
public class BackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);

    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("dev").isEmpty()) {
                userRepository.save(User.of("dev",
                        encoder.encode("password"),
                        "ArtJDev",
                        "test@mail.ru",
                        "+7-495",
                        Set.of(Role.of("ROLE_admin"), Role.of("ROLE_manager"))));
            }
        };
    }
}
