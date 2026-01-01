package fr.elikia.backend.bo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "workshop")
public class Workshop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workshopId;

    private String title;
    private String description;
    private LocalDateTime date;
    private int capacity;

    @Enumerated(EnumType.STRING)
    private Visibility visibility;


    // ========================================================
    // Constructors
    // ========================================================

    protected Workshop() {
    }

    public Workshop(String title, String description,
                    LocalDateTime date, int capacity, Visibility visibility) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.capacity = capacity;
        this.visibility = visibility;
    }


    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getWorkshopId() {
        return workshopId;
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
}
