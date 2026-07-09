package fr.elikia.backend.dto;

/**
 * Common contract for registration request DTOs.
 *
 * This interface exposes the shared registration
 * information required by the abstract registration service.
 */
public interface RegistrationRequestDTO {

    /**
     * Returns the participant first name.
     *
     * @return first name
     */
    String getFirstName();

    /**
     * Returns the participant last name.
     *
     * @return last name
     */
    String getLastName();

    /**
     * Returns the participant email address.
     *
     * @return email address
     */
    String getEmail();
}