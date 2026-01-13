package fr.elikia.backend.repository;

import fr.elikia.backend.bo.WorkshopRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkshopRegistrationRepository extends JpaRepository<WorkshopRegistration, Long> {
}
