package fr.elikia.backend.dto;

import fr.elikia.backend.bo.Media;
import fr.elikia.backend.bo.enums.Visibility;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO used to expose workshop information to the frontend.
 *
 * This DTO prevents exposing JPA relationships and avoids
 * circular references during JSON serialization.
 */
public class WorkshopResponseDTO {

    private Long workshopId;

    private String title;

    private String description;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String location;

    private String address;

    private int capacity;

    private Visibility visibility;

    private List<Media> mediaList;

    // ========================================================
    // Constructors
    // ========================================================

    public WorkshopResponseDTO() {
    }

    public WorkshopResponseDTO(
            Long workshopId,
            String title,
            String description,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String location,
            String address,
            int capacity,
            Visibility visibility,
            List<Media> mediaList
    ) {
        this.workshopId = workshopId;
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.address = address;
        this.capacity = capacity;
        this.visibility = visibility;
        this.mediaList = mediaList;
    }

    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(Long workshopId) {
        this.workshopId = workshopId;
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

    public List<Media> getMediaList() {
        return mediaList;
    }

    public void setMediaList(List<Media> mediaList) {
        this.mediaList = mediaList;
    }
}