package fr.elikia.backend.repository;

import fr.elikia.backend.bo.News;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsRepository extends JpaRepository<News, Long> {
}
