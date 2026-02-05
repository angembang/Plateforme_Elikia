package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.ContentStatus;
import fr.elikia.backend.bo.News;
import fr.elikia.backend.bo.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface IDAONews {
    List<News> findAll();

    List<News> findAllDesc();

    List<News> findAllScheduledToPublish(LocalDateTime now);

    List<News> findAllPublishedNews(ContentStatus contentStatus);

    List<News> findLastPublishedNews(int limit);

    Page<News> findPublishedNewsPage(ContentStatus contentStatus, Pageable pageable);

    Page<News> findAllByContentStatusAndVisibilityAfterOrderByPublishedAtDesc(
            ContentStatus contentStatus,
            Visibility visibility,
            Pageable pageable
    );

    News findById(Long newsId);

    boolean deleteById(Long newsId);

    News create(News news);

    News update(News news);

}
