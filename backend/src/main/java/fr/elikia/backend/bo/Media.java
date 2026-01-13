package fr.elikia.backend.bo;

import jakarta.persistence.*;

@Entity
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaId;

    private String caption;
    private String imagePath;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "workshop_id", nullable = true)
    private Workshop workshop;

    @ManyToOne
    @JoinColumn(name = "news_id", nullable = true)
    private News news;

    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = true)
    private Achievement achievement;



    // ========================================================
    // Constructors
    // ========================================================

    public Media() {
    }

    public Media(Long mediaId, String caption, String imagePath, String videoUrl, Event event, Workshop workshop,
                 News news, Achievement achievement) {
        this.mediaId = mediaId;
        this.caption = caption;
        this.imagePath = imagePath;
        this.videoUrl = videoUrl;
        this.event = event;
        this.workshop = workshop;
        this.news = news;
        this.achievement = achievement;
    }


    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getMediaId() {
        return mediaId;
    }

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

    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    public Workshop getWorkshop() {
        return workshop;
    }
    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }

    public News getNews() {
        return news;
    }
    public void setNews(News news) {
        this.news = news;
    }

    public Achievement getAchievement() {
        return achievement;
    }
    public void setAchievement(Achievement achievement) {
        this.achievement = achievement;
    }
}
