package fr.elikia.backend.media;

import fr.elikia.backend.bll.MediaService;
import fr.elikia.backend.bo.*;
import fr.elikia.backend.dao.idao.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class MediaServiceTest {

    // MOCKED IDAO DEPENDENCIES
    @Mock
    private IDAONews idaoNews;

    @Mock
    private IDAOEvent idaoEvent;

    @Mock
    private IDAOWorkshop idaoWorkshop;

    @Mock
    private IDAOAchievement idaoAchievement;

    @Mock
    private IDAOMedia idaoMedia;

    // SERVICE UNDER TEST
    @InjectMocks
    private MediaService mediaService;


    // TEST FIXTURES
    private News existingNews;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        // Build a valid News entity
        existingNews = new News();
        existingNews.setTitle("Test News");

        // Force the JPA ID so that findById returns the exact same object
        Field idField = News.class.getDeclaredField("newsId");
        idField.setAccessible(true);
        idField.set(existingNews, 1L);
    }


    // TEST SUCCESS CASE  (MEDIA FOR NEWS)
    @Test
    void createMediaShouldReturn201WhenValidMediaForNews() {
        // Arrange
        when(idaoNews.findById(1L)).thenReturn(existingNews);
        when(idaoMedia.create(any())).thenAnswer(invocation -> invocation.getArgument(0));

        MultipartFile file = mockImage();

        // Act
        LogicResult<Media> result =
                mediaService.createMedia(
                        file,
                        null, // videoUrl
                        "Valid caption", // caption
                        1L, // newsId
                        null,
                        null,
                        null
                );

        // Assert
        assertEquals("201", result.getCode());
        Media media = result.getData();

        assertEquals("Valid caption", media.getCaption());
        assertNotNull(media.getImagePath());
        assertTrue(media.getImagePath().endsWith(".jpg"));

        assertEquals(existingNews, media.getNews());
        assertTrue(existingNews.getMediaList().contains(media));

        // Verify
        verify(idaoNews, times(2)).findById(1L);
    }


    // Error no owner provided
    @Test
    void createMediaShouldReturn400WhenNoOwnerProvided() {
        MultipartFile file = mockImage();

        LogicResult<Media> result =
                mediaService.createMedia(
                        file,
                        null,
                        "Caption",
                        null, null, null, null
                );

        assertEquals("400", result.getCode());
        assertTrue(result.getMessage().contains("exactly one"));

        // No DAO should be called
        verifyNoInteractions(idaoNews, idaoEvent, idaoWorkshop, idaoAchievement);
    }


    // Error invalid youTube
    @Test
    void createMediaShouldReturn400WhenVideoUrlIsNotYouTube() {
        MultipartFile file = mockImage();

        LogicResult<Media> result =
                mediaService.createMedia(
                        file,
                        "https://vimeo.com/123456",
                        "Caption",
                        1L, null, null, null
                );

        assertEquals("400", result.getCode());
        assertTrue(result.getMessage().toLowerCase().contains("youtube"));
    }


    @Test
    void shouldUpdateMediaSuccessfully() {
        Media existing = new Media();

        existing.setNews(existingNews);

        when(idaoMedia.findById(5L)).thenReturn(existing);
        when(idaoMedia.update(any())).thenReturn(existing);

        MultipartFile newFile = mockImage();

        LogicResult<Void> result = mediaService.updateMedia(
                5L,
                newFile,
                null,
                "New caption"
        );

        assertEquals("200", result.getCode());
        assertEquals("New caption", existing.getCaption());
        assertNotNull(existing.getImagePath());
        assertTrue(existing.getImagePath().endsWith(".jpg"));
        assertEquals("Test News", existing.getNews().getTitle());
    }


    // Helper method
    private MultipartFile mockImage() {
        return new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/png",
                "fake-image-content".getBytes()
        );
    }

}
