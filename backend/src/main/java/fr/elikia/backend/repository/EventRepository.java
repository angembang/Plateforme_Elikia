package fr.elikia.backend.repository;

import fr.elikia.backend.bo.ContentStatus;
import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.News;
import fr.elikia.backend.bo.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Retrieve the event by its visibility ordered by the start date desc
     */
    Page<Event> findAllByVisibilityOrderByStartDateDesc(
            Visibility visibility,
            Pageable pageable
    );


    /**
     * Retrieve the all events ordered by the start date
     */
    Page<Event> findAllByOrderByStartDateDesc(
            Pageable pageable
    );


    /**
     * Retrieve the all events ordered by the start date limit 4
     */
    @Query("""
        SELECT e FROM Event e
            WHERE e.startDate IS NOT NULL
            ORDER BY e.startDate DESC
                LIMIT 4
    """)
    List<Event> findAllByOrderByStartDateDesc();
}
