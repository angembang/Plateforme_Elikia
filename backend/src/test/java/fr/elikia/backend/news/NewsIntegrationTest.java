package fr.elikia.backend.news;

import fr.elikia.backend.bll.NewsService;
import fr.elikia.backend.bo.ContentStatus;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.News;
import fr.elikia.backend.bo.Visibility;
import fr.elikia.backend.dao.idao.IDAONews;
import fr.elikia.backend.dto.NewsDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class NewsIntegrationTest {
    @Autowired
    private NewsService newsService;

    @Autowired
    private IDAONews idaoNews;

    @Test
    void shouldPersistNewsInDatabase() {
        // Arrange
        NewsDTO dto = new NewsDTO();
        dto.setTitle("Integration title");
        dto.setContent("Integration content");
        dto.setPublishedAt(LocalDateTime.now().plusDays(1));
        dto.setVisibility(Visibility.PUBLIC);
        dto.setContentStatus(ContentStatus.CREATED);

        // Act
        LogicResult<Void> result =
                newsService.createNews(dto, null);

        // Assert
        assertEquals("201", result.getCode());

        // Verify in database
        List<News> allNews = idaoNews.findAll();
        assertEquals(1, allNews.size());

        News persisted = allNews.getFirst();
        assertEquals("Integration title", persisted.getTitle());
    }
}
