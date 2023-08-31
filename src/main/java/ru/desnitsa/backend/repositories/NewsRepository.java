package ru.desnitsa.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.desnitsa.backend.entities.News;

public interface NewsRepository extends CrudRepository<News, Long> {

}
