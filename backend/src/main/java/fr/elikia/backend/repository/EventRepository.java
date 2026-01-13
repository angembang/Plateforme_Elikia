package fr.elikia.backend.repository;

import fr.elikia.backend.bo.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
