package ru.desnitsa.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.desnitsa.backend.entities.About;

import java.util.Optional;

public interface AboutRepository extends CrudRepository<About, Long> {

    Optional<About> findAboutByConstant(String constant);

}
