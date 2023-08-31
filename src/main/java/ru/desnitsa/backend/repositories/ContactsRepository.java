package ru.desnitsa.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.desnitsa.backend.entities.Contacts;

@Repository
public interface ContactsRepository extends CrudRepository<Contacts, Long> {
}
