package fr.elikia.backend.repository;

import fr.elikia.backend.bo.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
