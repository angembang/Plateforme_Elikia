package fr.elikia.backend.bo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "news")
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsId;

    private String title;
    private String content;
    private LocalDateTime publishedAt;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;


    // ========================================================
    // Constructors
    // ========================================================

    protected News() {
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
}
