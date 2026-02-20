package fr.elikia.backend.bo;

import fr.elikia.backend.bo.enums.Visibility;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long achievementId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @OneToMany(
            mappedBy = "achievement",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Media> mediaList = new ArrayList<>();


    // ========================================================
    // Constructors
    // ========================================================

    public Achievement() {
    }

    public Achievement(Long achievementId, String title, String description, LocalDateTime date, Visibility visibility) {
        this.achievementId = achievementId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.visibility = visibility;
    }


    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getAchievementId() {
        return achievementId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Visibility getVisibility() {
        return visibility;
    }
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }


    /**
     * Helper methods to manage the bidirectional relationship between
     * the parent entity (Achievement)
     * and its associated Media.
     * These methods ensure consistency on both sides of the association.
     */

    public void addMedia(Media media) {
        mediaList.add(media);
        media.setAchievement(this);
    }

    public void removeMedia(Media media) {
        mediaList.remove(media);
        media.setAchievement(null);
    }
}
