package fr.elikia.backend.repository;

import fr.elikia.backend.bo.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
}
