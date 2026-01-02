package fr.elikia.backend.bo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long achievementId;

    private String title;
    private String description;
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;


    // ========================================================
    // Constructors
    // ========================================================

    public Achievement() {
    }

    public Achievement(Long achievementId, String title, String description, LocalDateTime date) {
        this.achievementId = achievementId;
        this.title = title;
        this.description = description;
        this.date = date;
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
}
