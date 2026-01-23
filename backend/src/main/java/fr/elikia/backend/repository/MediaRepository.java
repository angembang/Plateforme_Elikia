package fr.elikia.backend.repository;

import fr.elikia.backend.bo.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {
    // Returns all media linked to a specific news
    List<Media> findAllByNews(News news);

    // Returns all media linked to a specific event
    List<Media> findAllByEvent(Event event);

    // Returns all media linked to a specific workshop
    List<Media> findAllByWorkshop(Workshop workshop);

    // Returns all media linked to a specific achievement
    List<Media> findAllByAchievement(Achievement achievement);
}
