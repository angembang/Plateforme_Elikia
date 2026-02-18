package fr.elikia.backend.dto;

import fr.elikia.backend.bo.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * DTO used for event registration.
 * Contains only fields allowed to be sent by the client.
 */
public class EventDTO {
    // Title of the event
    @Schema(
            description = "Titre de l'évènement",
            example = "la nuit du conte"
    )
    @NotBlank(message = "the title is required")
    @Size(min = 3, max = 255, message = "The title must be between 3 and 255 characters")
    private String title;


    // Description
    @Schema(
            description = "Description de l'évènement",
            example = "la Nuit du conte, des milliers d’enfants et de jeunes " +
                    "chercheront une nouvelle fois leur bonheur dans des histoires. " +
                    "Ils partageront leur excitation pour les personnages ayant de la chance" +
                    " dans le malheur. Ils encourageront ceux qui partent dans le monde à leurs risques" +
                    " et périls. Ils discuteront de ce que le bonheur signifie pour eux"
    )
    @NotBlank(message = "The description is required")
    @Size(min = 3, max = 2000, message = "The content must be between 3 and 1500 characters")
    private String description;


    // Start date
    @Schema(
            description = "Date de début de l'évènement",
            example = "14/12/2011 à 11h00"
    )
    private LocalDateTime startDate;


    // End date
    @Schema(
            description = "Date de fin de l'évènement",
            example = "14/12/2011 à 22h00"
    )
    private LocalDateTime endDate;


    // Location of the event
    @Schema(
            description = "Lieu de l'évènement",
            example = "Centre Culturel de Rennes"
    )
    @NotBlank(message = "the location is required")
    @Size(min = 2, max = 100, message = "The location must be between 2 and 100 characters")
    private String location;


    // Address of the event
    @Schema(
            description = "Adresse de l'évènement",
            example = "4 rue Réné Dumont "
    )
    @NotBlank(message = "the address is required")
    @Size(min = 5, max = 255, message = "The address must be between 5 and 255 characters")
    private String address;


    // capacity (number of places) of the event
    @Schema(
            description = "Nombre de place pour l'évènement",
            example = "400"
    )
    @Min(value = 1, message = "The capacity must be at least 1")
    @Max(value = 50000, message = "The capacity is too large")
    private int capacity;


    // Visibility level
    @Schema(
            description = "visibilité de l'évènement",
            example = "PUBLIC, MEMBER_ONLY "
    )
    @NotNull(message = "The visibility is required")
    private Visibility visibility;


    // Constructor
    public EventDTO() {}

    public EventDTO(String title, String description, LocalDateTime startDate,
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
