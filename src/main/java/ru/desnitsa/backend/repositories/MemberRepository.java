package ru.desnitsa.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.desnitsa.backend.entities.Member;
@Repository
public interface MemberRepository extends CrudRepository<Member, Long> {
}
