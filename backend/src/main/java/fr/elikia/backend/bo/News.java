package fr.elikia.backend.bo;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity  // Marks the class as a JPA entity mapped to a database table
@Table(name = "news") // Specifies the table name in the database
public class News {
    // Primary key of the entity
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long newsId;

    // Title is mandatory
    @Column(nullable = false)
    private String title;

    // Content is mandatory
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // Publication date time of the news
    private LocalDateTime publishedAt;

    @Enumerated(EnumType.STRING)  // Store enum as String in DB
    @Column(nullable = false)
    private Visibility visibility;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus contentStatus;

    /**
     * One News can have many Media.
     * mappedBy = "news" refers to the field in Media entity.
     * Cascade.ALL: persist/update/delete Media automatically with News.
     * orphanRemoval = true: remove Media if it is removed from the list.
     */
    @OneToMany(
            mappedBy = "news",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Media> mediaList = new ArrayList<>();


    // Default constructor required by JPA
    public News() {
    }

    // Convenience constructor
    public News(String title, String content,
                LocalDateTime publishedAt, Visibility visibility) {
        this.title = title;
        this.content = content;
        this.publishedAt = publishedAt;
        this.visibility = visibility;
    }


    // Getters and setters
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

    // Adds a Media to this News and sets the reverse relation
    public void addMedia(Media media) {
        mediaList.add(media);
        media.setNews(this);
    }

    // Removes a Media from this News and clears the reverse relation
    public void removeMedia(Media media) {
        mediaList.remove(media);
        media.setNews(null);
    }
}
