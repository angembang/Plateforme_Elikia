package fr.elikia.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

/**
 * DTO used for news registration.
 * Contains only fields allowed to be sent by the client.
 */
public class MediaDTO {
    // Caption of the media
    @Schema(
            description = "le titre du media",
            example = "logo de l'Association Elikia"
    )
    @Size(min = 3, max = 255, message = "The caption must be between 3 and 255 characters")
    private String caption;

    // Path to the image file
    @Schema(
            description = "le chemin de l'image media"
    )
    private String imagePath;

    // URL of the video
    @Schema(
            description = "le chemin de la video"
    )
    private String videoUrl;

    // Foreign key references (only IDs, not entities)
    @Schema(
            description = ""
    )
    private Long newsId;

    @Schema(
            description = ""
    )
    private Long eventId;

    @Schema(
            description = ""
    )
    private Long workshopId;

    @Schema(
            description = ""
    )
    private Long achievementId;

    // Constructors
    public MediaDTO() {}

    public MediaDTO(String caption, String imagePath, String videoUrl, Long newsId,
                    Long eventId, Long workshopId, Long achievementId) {
        this.caption = caption;
        this.imagePath = imagePath;
        this.videoUrl = videoUrl;
        this.newsId = newsId;
        this.eventId = eventId;
        this.workshopId = workshopId;
        this.achievementId = achievementId;
    }

    // Getters & Setters
    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(Long workshopId) {
        this.workshopId = workshopId;
    }

    public Long getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(Long achievementId) {
        this.achievementId = achievementId;
    }
}
