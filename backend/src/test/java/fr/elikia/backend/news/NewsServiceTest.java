package fr.elikia.backend.news;

import fr.elikia.backend.bll.MediaService;
import fr.elikia.backend.bll.NewsService;
import fr.elikia.backend.bo.enums.ContentStatus;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.News;
import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.dao.idao.IDAONews;
import fr.elikia.backend.dto.NewsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {
    // MOCKED IDAO DEPENDENCIES
    @Mock
    private IDAONews idaoNews;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private NewsService newsService;


    // Helper methods
    private NewsDTO buildValidNewsDTO() {
        NewsDTO dto = new NewsDTO();
        dto.setTitle("Valid title");
        dto.setContent("Valid content");
        dto.setPublishedAt(LocalDateTime.now().plusDays(1));
        dto.setVisibility(Visibility.PUBLIC);
        dto.setContentStatus(ContentStatus.PUBLISHED);
        return dto;
    }


    private MultipartFile mockImage() {
        return new MockMultipartFile(
                "file",
                "bad.exe",
                "image/png",
                "fake-image-content".getBytes()
        );
    }


    // Success case
    @Test
    void shouldCreateNewsWithoutMediaSuccessfully() {
        // Arrange
        NewsDTO newsDTO = buildValidNewsDTO();

        // Mock DAO create behavior
        doAnswer(invocation -> {
            News news = invocation.getArgument(0);
            ReflectionTestUtils.setField(news, "newsId", 1L);
            return null;
        }).when(idaoNews).create(any(News.class));

        when(idaoNews.update(any(News.class)))
                .thenAnswer(invocation ->invocation.getArgument(0));

        // Act
        LogicResult<Void> result =
                newsService.createNews(newsDTO, null);

        // Assert
        assertEquals("201", result.getCode());
        assertEquals("News created successfully with uploaded medias", result.getMessage());

        verify(idaoNews).create(any(News.class));
        verify(idaoNews).update(any(News.class));
        verifyNoInteractions(mediaService);
    }


    // Validation error case
    @Test
    void shouldFailWhenTitleIsBlank() {
        // Arrange
        NewsDTO newsDTO = buildValidNewsDTO();
        newsDTO.setTitle(" "); // invalid

        // Act
        LogicResult<Void> result =
                newsService.createNews(newsDTO, null);

        // Assert
        assertEquals("400", result.getCode());
        assertEquals("The title is required", result.getMessage());

        verifyNoInteractions(idaoNews);
        verifyNoInteractions(mediaService);
    }


    // Media failure propagation
    @Test
    void shouldFailWhenMediaCreationFails() {
        // Arrange
        NewsDTO newsDTO = buildValidNewsDTO();

        MultipartFile file = mockImage();
        List<MultipartFile> files = List.of(file);

        doAnswer(invocation -> {
            News news = invocation.getArgument(0);
            ReflectionTestUtils.setField(news, "newsId", 1L);
            return null;
        }).when(idaoNews).create(any(News.class));

        when(mediaService.createMedia(
                any(MultipartFile.class),
                any(),
                any(),
                anyLong(),
                any(),
                any(),
                any()
        )).thenReturn(
                new LogicResult<>("400", "Invalid image", null)
        );

        // Act
        LogicResult<Void> result =
                newsService.createNews(newsDTO, files);

        // Assert
        assertEquals("400", result.getCode());
        assertTrue(result.getMessage().startsWith("Media creation failed"));

        verify(idaoNews).create(any(News.class));
        verify(mediaService).createMedia(
                any(MultipartFile.class),
                any(),
                any(),
                anyLong(),
                any(),
                any(),
                any()
        );
        verify(idaoNews, never()).update(any());
    }

}
