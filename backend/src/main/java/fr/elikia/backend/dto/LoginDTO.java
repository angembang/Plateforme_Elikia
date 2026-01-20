package fr.elikia.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO used for user login.
 * Contains only fields allowed to be sent by the client.
 */
public class LoginDTO {
    @Schema(
            description = "Adresse email de l'utilisateur",
            example = "user@mail.com"
    )
    @NotBlank
    @Email
    private String email;

    @Schema(
            description = "Mot de passe",
            format = "password",
            accessMode = Schema.AccessMode.WRITE_ONLY
    )
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    // Constructor
    public LoginDTO(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Getters & Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
