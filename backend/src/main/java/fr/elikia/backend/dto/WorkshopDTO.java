package fr.elikia.backend.dto;

import fr.elikia.backend.bo.enums.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * DTO used for workshop registration.
 * Contains only fields allowed to be sent by the client.
 */
public class WorkshopDTO implements ActivityDTO {
    // Title of the workshop
    @Schema(
            description = "Titre de l'atelier",
            example = "Atelier d'éducation citoyenne"
    )
    @NotBlank(message = "the title is required")
    @Size(min = 3, max = 255, message = "The title must be between 3 and 255 characters")
    private String title;


    // Description
    @Schema(
            description = "Description de l'atelier",
            example = "Cet atelier sensibilise les jeunes aux valeurs citoyennes, aux droits et devoirs, " +
                    "à la participation communautaire et au respect des règles sociales."
    )
    @NotBlank(message = "The description is required")
    @Size(min = 3, max = 2000, message = "The content must be between 3 and 1500 characters")
    private String description;


    // Start date
    @Schema(
            description = "Date de début de l'atelier",
            example = "19/12/2012 à 11h00"
    )
    private LocalDateTime startDate;


    // End date
    @Schema(
            description = "Date de fin de l'atelier",
            example = "19/12/2012 à 22h00"
    )
    private LocalDateTime endDate;


    // Location of the workshop
    @Schema(
            description = "Lieu de l'atelier",
            example = "Siège de l'Association Elikia"
    )
    @NotBlank(message = "the location is required")
    @Size(min = 2, max = 100, message = "The location must be between 2 and 100 characters")
    private String location;


    // Address of the workshop
    @Schema(
            description = "Adresse de l'atelier",
            example = "4 rue Réné Dumont "
    )
    @NotBlank(message = "the address is required")
    @Size(min = 5, max = 255, message = "The address must be between 5 and 255 characters")
    private String address;


    // capacity (number of places) of the workshop
    @Schema(
            description = "Nombre de place pour l'atelier",
            example = "400"
    )
    @Min(value = 1, message = "The capacity must be at least 1")
    @Max(value = 50000, message = "The capacity is too large")
    private int capacity;


    // Visibility level
    @Schema(
            description = "visibilité de l'atelier",
            example = "PUBLIC, MEMBER_ONLY "
    )
    @NotNull(message = "The visibility is required")
    private Visibility visibility;


    // Constructor
    public WorkshopDTO() {}

    public WorkshopDTO(String title, String description, LocalDateTime startDate,
                    LocalDateTime endDate, String location, String address,
                    int capacity, Visibility visibility) {
        this.title = title;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.address = address;
        this.capacity = capacity;
        this.visibility = visibility;
    }


    // Getters && Setters
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
}
