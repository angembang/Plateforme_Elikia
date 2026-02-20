package fr.elikia.backend.repository;

import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.bo.Workshop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WorkshopRepository extends JpaRepository<Workshop, Long> {
    /**
     * Retrieve the workshop by its visibility ordered by the start date desc
     */
    Page<Workshop> findAllByVisibilityOrderByStartDateDesc(
            Visibility visibility,
            Pageable pageable
    );


    /**
     * Retrieve all workshops ordered by the start date
     */
    Page<Workshop> findAllByOrderByStartDateDesc(
            Pageable pageable
    );


    /**
     * Retrieve 4 latest workshops
     */
    @Query("""
        SELECT w FROM Workshop w
            WHERE w.startDate IS NOT NULL
            ORDER BY w.startDate DESC
                LIMIT 4
    """)
    List<Workshop> findAllByOrderByStartDateDesc();
}
