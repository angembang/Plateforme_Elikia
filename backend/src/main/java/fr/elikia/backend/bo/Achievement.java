package fr.elikia.backend.bo;

import jakarta.persistence.*;

@Entity
@Table(name = "achievement")
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long achievementId;

    private String title;
    private String description;


    // ========================================================
    // Constructors
    // ========================================================

    public Achievement() {
    }

    public Achievement(Long achievementId, String title, String description) {
        this.achievementId = achievementId;
        this.title = title;
        this.description = description;
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
}
