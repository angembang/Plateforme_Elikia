package fr.elikia.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO used for role registration.
 * Contains only fields allowed to be sent by the client.
 */
public class RoleDTO {
    @NotBlank
    @Size(min = 2, max = 30, message = "name must be between 2 and 30 characters")
    private String name;

    // Constructor
    public RoleDTO(String name) {
        this.name = name;
    }

    // Getters & Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}