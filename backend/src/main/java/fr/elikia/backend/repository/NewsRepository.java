package fr.elikia.backend.repository;

import fr.elikia.backend.bo.ContentStatus;
import fr.elikia.backend.bo.News;
import fr.elikia.backend.bo.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findAllByContentStatusOrderByPublishedAtDesc(ContentStatus contentStatus);

    @Query("""
        SELECT n FROM News n
            WHERE n.publishedAt IS NOT NULL
            ORDER BY n.publishedAt DESC
    """)
    List<News> findAllPublishedOrderByPublishedAtDesc();

    Page<News> findAllByContentStatusOrderByPublishedAtDesc(
            ContentStatus contentStatus,
            Pageable pageable
    );

    Page<News> findAllByContentStatusAndVisibilityAfterOrderByPublishedAtDesc(
            ContentStatus contentStatus,
            Visibility visibility,
            Pageable pageable
    );

    @Query("""
        SELECT n FROM News n
        WHERE n.contentStatus = 'CREATED'
          AND n.publishedAt IS NOT NULL
          AND n.publishedAt <= :now
    """)
    List<News> findAllScheduledToPublish(@Param("now") LocalDateTime now);


}
