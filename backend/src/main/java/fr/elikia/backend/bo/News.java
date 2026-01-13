package fr.elikia.backend.bo;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    private LocalDateTime publishedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus contentStatus;

    @OneToMany(
            mappedBy = "news",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Media> mediaList = new ArrayList<>();


    // ========================================================
    // Constructors
    // ========================================================

    public News() {
    }

    public News(String title, String content,
                LocalDateTime publishedAt, Visibility visibility) {
        this.title = title;
        this.content = content;
        this.publishedAt = publishedAt;
        this.visibility = visibility;
    }


    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getNewsId() {
        return newsId;
    }

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

    public List<Media> getMediaList() {
        return mediaList;
    }


    /**
     * Helper methods to manage the bidirectional relationship between
     * the parent entity (News)
     * and its associated Media.
     * These methods ensure consistency on both sides of the association.
     */

    public void addMedia(Media media) {
        mediaList.add(media);
        media.setNews(this);
    }

    public void removeMedia(Media media) {
        mediaList.remove(media);
        media.setNews(null);
    }
}
