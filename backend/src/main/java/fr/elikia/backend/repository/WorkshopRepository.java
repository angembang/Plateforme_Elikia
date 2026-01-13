package fr.elikia.backend.repository;

import fr.elikia.backend.bo.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopRepository extends JpaRepository<Workshop, Long> {
}
