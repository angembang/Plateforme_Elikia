package fr.elikia.backend.news;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.elikia.backend.bo.ContentStatus;
import fr.elikia.backend.bo.Visibility;
import fr.elikia.backend.dto.NewsCreationRequest;
import fr.elikia.backend.dto.NewsDTO;
import fr.elikia.backend.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class NewsControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    // =========================
    // Helper
    // =========================
    private NewsCreationRequest buildValidRequest() {
        NewsDTO newsDTO = new NewsDTO();
        newsDTO.setTitle("API title");
        newsDTO.setContent("API content");
        newsDTO.setPublishedAt(LocalDateTime.now().plusDays(1));
        newsDTO.setVisibility(Visibility.PUBLIC);
        newsDTO.setContentStatus(ContentStatus.PUBLISHED);

        NewsCreationRequest request = new NewsCreationRequest();
        request.setNews(newsDTO);
        request.setMediaList(null);

        return request;
    }

    // ---------- Success case (ADMIN) --------------------------
    @Test
    void shouldReturn201WhenAdminCreatesNews() throws Exception {
        // Arrange
        NewsCreationRequest request = buildValidRequest();

        String adminToken = jwtService.generateToken("admin@mail.com", "ADMIN");

        // Act & Assert
        MockMultipartFile newsPart = new MockMultipartFile(
                "news", // must match @RequestPart("news")
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request.getNews())
        );

        mockMvc.perform(
                        multipart("/api/news/add")
                                .file(newsPart)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.message").value("News created successfully with uploaded medias"));
    }

    // =========================
    // Forbidden case (MEMBER)
    // =========================
    @Test
    void shouldReturn403WhenMemberCreatesNews() throws Exception {
        // Arrange
        NewsCreationRequest request = buildValidRequest();

        String memberToken = jwtService.generateToken("member@mail.com", "MEMBER");

        // Act & Assert
        MockMultipartFile newsPart = new MockMultipartFile(
                "news",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request.getNews())
        );

        mockMvc.perform(
                        multipart("/api/news/add")
                                .file(newsPart)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + memberToken)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isForbidden());
    }

    // =========================
    // Unauthorized case (no token)
    // =========================
    @Test
    void shouldReturn401WhenNoTokenProvided() throws Exception {
        // Arrange
        NewsCreationRequest request = buildValidRequest();

        // Act & Assert
        MockMultipartFile newsPart = new MockMultipartFile(
                "news",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request.getNews())
        );

        mockMvc.perform(
                        multipart("/api/news/add")
                                .file(newsPart)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isUnauthorized());
    }

    // =========================
    // Validation error case
    // =========================
    @Test
    void shouldReturn400WhenTitleMissing() throws Exception {
        // Arrange
        NewsCreationRequest request = buildValidRequest();
        request.getNews().setTitle(""); // invalid

        String adminToken = jwtService.generateToken("admin@mail.com", "ADMIN");

        // Act & Assert
        MockMultipartFile newsPart = new MockMultipartFile(
                "news",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request.getNews())
        );

        mockMvc.perform(
                        multipart("/api/news/add")
                                .file(newsPart)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.message").value("The title is required"));
    }
}
