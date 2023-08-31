package ru.desnitsa.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.desnitsa.backend.entities.Tour;

public interface TourRepository extends CrudRepository<Tour, Long> {
}
