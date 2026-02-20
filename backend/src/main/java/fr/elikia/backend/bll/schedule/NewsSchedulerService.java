package fr.elikia.backend.bll.schedule;

import fr.elikia.backend.bo.enums.ContentStatus;
import fr.elikia.backend.bo.News;
import fr.elikia.backend.dao.idao.IDAONews;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NewsSchedulerService {
    private final IDAONews idaoNews;

    public NewsSchedulerService(IDAONews idaoNews) {
        this.idaoNews = idaoNews;
    }

    /**
     * Publishes scheduled news when publication date is reached
     */
    @Scheduled(fixedRate = 60000) // all the minute
    @Transactional
    public void publishScheduledNews() {

        List<News> toPublish =
                idaoNews.findAllScheduledToPublish(LocalDateTime.now());

        for (News news : toPublish) {
            news.setContentStatus(ContentStatus.PUBLISHED);
            idaoNews.update(news);
        }
    }
}
