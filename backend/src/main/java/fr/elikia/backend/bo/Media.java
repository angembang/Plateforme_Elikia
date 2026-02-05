package fr.elikia.backend.bo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity // Marks this class as a JPA entity
@Table(name = "media") // Maps to media table
public class Media {
    // Primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mediaId;

    // Caption or title of the media
    private String caption;
    // Path to the stored image file
    private String imagePath;
    // URL of the video if the media is a video
    private String videoUrl;

    /**
     * A Media can belong to one Event (optional).
     */
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = true)
    @JsonIgnore
    private Event event;

    /**
     * A Media can belong to one Workshop (optional).
     */
    @ManyToOne
    @JoinColumn(name = "workshop_id", nullable = true)
    @JsonIgnore
    private Workshop workshop;

    /**
     * A Media can belong to one News (optional).
     */
    @ManyToOne
    @JoinColumn(name = "news_id", nullable = true)
    @JsonIgnore
    private News news;

    /**
     * A Media can belong to one Achievement (optional).
     */
    @ManyToOne
    @JoinColumn(name = "achievement_id", nullable = true)
    @JsonIgnore
    private Achievement achievement;



    // Default constructor required by JPA
    public Media() {
    }

    // Full constructor
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


    // Getters and setters

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
