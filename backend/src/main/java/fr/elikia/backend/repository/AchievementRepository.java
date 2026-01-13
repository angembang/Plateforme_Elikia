package fr.elikia.backend.repository;

import fr.elikia.backend.bo.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AchievementRepository extends JpaRepository<Achievement, Long> {
}
