package ru.desnitsa.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.desnitsa.backend.entities.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
