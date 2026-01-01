package fr.elikia.backend.bo;

import jakarta.persistence.*;

@Entity
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaId;

    private String imageUrl;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "workshop_id")
    private Workshop workshop;

    @ManyToOne
    @JoinColumn(name = "news_id")
    private News news;

    @ManyToOne
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;



    // ========================================================
    // Constructors
    // ========================================================

    public Media() {
    }

    public Media(Long mediaId, String imageUrl, String videoUrl, Event event, Workshop workshop,
                 News news, Achievement achievement) {
        this.mediaId = mediaId;
        this.imageUrl = imageUrl;
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

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
