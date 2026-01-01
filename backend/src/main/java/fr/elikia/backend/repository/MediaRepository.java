package fr.elikia.backend.repository;

import fr.elikia.backend.bo.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
