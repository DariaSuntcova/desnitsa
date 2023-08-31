package ru.desnitsa.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.desnitsa.backend.entities.Partner;

public interface PartnerRepository extends CrudRepository<Partner, Long> {
}
