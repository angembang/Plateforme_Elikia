package fr.elikia.backend.repository;

import fr.elikia.backend.bo.Donation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DonationRepository extends JpaRepository<Donation, Long> {
}
