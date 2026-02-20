package fr.elikia.backend.dto;

import fr.elikia.backend.bo.enums.ContentStatus;
import fr.elikia.backend.bo.enums.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * DTO used for news registration.
 * Contains only fields allowed to be sent by the client.
 */
public class NewsDTO {
    // Title of the news
    @Schema(
            description = "Titre de l'actualité",
            example = "Rennes: l'Association Elikia organise la nuit du conte ce 2 février au CDAS de Maurepas"
    )
    @NotBlank(message = "the title is required")
    @Size(min = 3, max = 255, message = "The title must be between 3 and 255 characters")
    private String title;

    // Content of the news
    @Schema(
            description = "Contenu de l'actualité",
            example = "Lors de la Nuit du conte, des milliers d’enfants et de jeunes " +
                    "chercheront une nouvelle fois leur bonheur dans des histoires. " +
                    "Ils partageront leur excitation pour les personnages ayant de la chance" +
                    " dans le malheur. Ils encourageront ceux qui partent dans le monde à leurs risques" +
                    " et périls. Ils discuteront de ce que le bonheur signifie pour eux"
    )
    @NotBlank(message = "The content is required")
    @Size(min = 3, max = 5000, message = "The content must be between 3 and 5000 characters")
    private String content;

    // Publication date
    @Schema(
            description = "Date de publication de l'actualité",
            example = "14/12/2011 à 11h00"
    )
    private LocalDateTime publishedAt;

    // Visibility level
    @Schema(
            description = "visibilité de l'actualité",
            example = "PUBLIC, MEMBER_ONLY, etc."
    )
    @NotNull(message = "The visibility is required")
    private Visibility visibility;

    // Status of the content
    @Schema(
            description = "Le statut de l'actualité",
            example = "CREATED, CANCELLED, etc."
    )
    @NotNull(message = "The content status is required")
    private ContentStatus contentStatus;

    // Constructors
    public NewsDTO() {}

    public NewsDTO(String title, String content, LocalDateTime publishedAt, Visibility visibility, ContentStatus contentStatus) {
        this.title = title;
        this.content = content;
        this.publishedAt = publishedAt;
        this.visibility = visibility;
        this.contentStatus = contentStatus;
    }


    // Getters & Setters

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public ContentStatus getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(ContentStatus contentStatus) {
        this.contentStatus = contentStatus;
    }
}
