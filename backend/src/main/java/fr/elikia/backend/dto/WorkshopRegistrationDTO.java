package fr.elikia.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO used to submit a workshop registration request.
 *
 * It contains the visitor or member information
 * required to register for a workshop.
 */
public class WorkshopRegistrationDTO implements RegistrationRequestDTO {

    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères.")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères.")
    private String lastName;

    @NotBlank(message = "L'adresse e-mail est obligatoire.")
    @Email(message = "L'adresse e-mail est invalide.")
    @Size(max = 255, message = "L'adresse e-mail ne peut pas dépasser 255 caractères.")
    private String email;

    // ========================================================
    // Constructors
    // ========================================================

    public WorkshopRegistrationDTO() {
    }

    public WorkshopRegistrationDTO(
            String firstName,
            String lastName,
            String email
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // ========================================================
    // Getters & Setters
    // ========================================================

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}