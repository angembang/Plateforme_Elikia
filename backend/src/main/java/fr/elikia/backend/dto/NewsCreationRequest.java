package fr.elikia.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;

import java.util.List;

/**
 * Wrapper DTO used to create News with its associated Media in a single request

 * This class represents a composite API request:
 * - One NewsDTO (mandatory)
 * - Zero or more MediaDTO (optional)

 * It is required because Spring can only deserialize ONE root JSON object
 */
@Schema(
        description = "Request object used to create a News with its associated Media"
)
public class NewsCreationRequest {
    /**
     * The News data to be created.
     * This field is mandatory.
     */
    @Valid // Triggers validation on NewsDTO fields
    @Schema(
            description = "The news to create",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private NewsDTO news;

    /**
     * Optional list of media associated with this news.
     * Can be null or empty.
     */
    @Valid // Triggers validation on each MediaDTO in the list
    @Schema(
            description = "Optional list of media linked to this news"
    )
    private List<MediaDTO> mediaList;

    // =========================
    // Constructors
    // =========================

    public NewsCreationRequest() {
        // Default constructor required by Jackson
    }

    public NewsCreationRequest(NewsDTO news, List<MediaDTO> mediaList) {
        this.news = news;
        this.mediaList = mediaList;
    }

    // =========================
    // Getters & Setters
    // =========================

    public NewsDTO getNews() {
        return news;
    }

    public void setNews(NewsDTO news) {
        this.news = news;
    }

    public List<MediaDTO> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<MediaDTO> mediaList) {
        this.mediaList = mediaList;
    }
}
