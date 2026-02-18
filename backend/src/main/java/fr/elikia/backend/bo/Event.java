package fr.elikia.backend.bo;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table( name = "event" )
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility;

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<EventRegistration> registrations = new ArrayList<>();

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private final List<Media> mediaList = new ArrayList<>();


    // ========================================================
    // Constructors
    // ========================================================

    public Event() {
    }

    public Event(String title, String description,
                 LocalDateTime startDate, LocalDateTime endDate, String location, String address, int capacity,
                 Visibility visibility) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.address = address;
        this.capacity = capacity;
        this.visibility = visibility;
    }


    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getEventId() {
        return eventId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description; }
    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Visibility getVisibility() {
        return visibility;
    }
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public List<EventRegistration> getRegistrations() {
        return registrations;
    }
    public void setRegistrations(List<EventRegistration> registrations) {
        this.registrations = registrations;
    }

    public List<Media> getMediaList() {
        return mediaList;
    }


    /**
     * Helper methods to manage the bidirectional relationship between
     * the parent entity (Event)
     * and its associated Media.
     * These methods ensure consistency on both sides of the association.
     */

    public void addMedia(Media media) {
        mediaList.add(media);
        media.setEvent(this);
    }

    public void removeMedia(Media media) {
        mediaList.remove(media);
        media.setEvent(null);
    }
}
