package fr.elikia.backend.dao;

import fr.elikia.backend.bo.ContentStatus;
import fr.elikia.backend.bo.News;
import fr.elikia.backend.dao.idao.IDAONews;
import fr.elikia.backend.repository.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NewsDAO implements IDAONews {
    private final NewsRepository newsRepository;

    public NewsDAO(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }
    public List<News> findAll() {
        return newsRepository.findAll();

    }


    @Override
    public List<News> findAllDesc() {
        return newsRepository.findAllPublishedOrderByPublishedAtDesc();
    }

    @Override
    public List<News> findAllScheduledToPublish(LocalDateTime now) {
        return newsRepository.findAllScheduledToPublish(now);
    }


    @Override
    public List<News> findAllPublishedNews(ContentStatus contentStatus) {
        return newsRepository.findAllByContentStatusOrderByPublishedAtDesc(ContentStatus.PUBLISHED);
    }


    @Override
    public Page<News> findPublishedNewsPage(ContentStatus contentStatus, Pageable pageable) {
        return newsRepository
                .findAllByContentStatusOrderByPublishedAtDesc(
                        contentStatus,
                        pageable
                );
    }


    @Override
    public List<News> findLastPublishedNews(int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        return newsRepository
                .findAllByContentStatusOrderByPublishedAtDesc(
                        ContentStatus.PUBLISHED,
                        pageable
                )
                .getContent();
    }


    @Override
    public News findById(Long newsId) {
        return newsRepository.findById(newsId).orElse(null);

    }


    @Override
    public boolean deleteById(Long newsId) {
        News news = newsRepository.findById(newsId).orElse(null);

        if(news != null) {
            newsRepository.delete(news);
           return true;
        }
        return false;

    }


    @Override
    public News create(News news) {
        return newsRepository.save(news);

    }


    @Override
    public News update(News news) {
        // Check if the news exists
        if(!newsRepository.existsById(news.getNewsId())) {
            return null;
        }
        return newsRepository.save(news);

    }
}
