package fr.elikia.backend.bll;

import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.dto.EventRegistrationDTO;

/**
 * Abstract service containing the common validation logic
 * shared by event and workshop registrations.
 *
 * This class helps reduce code duplication by providing
 * reusable validation methods for registration workflows.
 */
public class AbstractRegistrationService {

    /**
     * Validate the activity identifier.
     *
     * This method is shared by different registration services
     * to validate the identifier of the activity
     * (event or workshop).
     *
     * @param activityId activity identifier
     * @return LogicResult containing an error if the identifier is invalid,
     *         or null if it is valid
     */
    protected LogicResult<Void> validateActivityId(Long activityId) {
        if (activityId == null || activityId <= 0) {
            return new LogicResult<>("400", "The activity identifier is required", null);
        }

        return null;
    }

    /**
     * Validate the registration identifier.
     *
     * This method ensures that the registration identifier
     * is present and valid before performing
     * any registration operation.
     *
     * @param registrationId registration identifier
     * @return LogicResult containing an error if the identifier is invalid,
     *         or null if it is valid
     */
    protected LogicResult<Void> validateRegistrationId(Long registrationId) {
        if (registrationId == null || registrationId <= 0) {
            return new LogicResult<>("400", "The registration identifier is required", null);
        }

        return null;
    }

    /**
     * Validate the common registration data.
     *
     * This method checks that the required registration
     * information is provided before creating
     * a new registration.
     *
     * @param registrationDTO registration data
     * @return LogicResult containing an error if the data is invalid,
     *         or null if the data is valid
     */
    protected LogicResult<Void> validateRegistrationData(EventRegistrationDTO registrationDTO) {
        if (registrationDTO == null) {
            return new LogicResult<>("400", "Registration data is required", null);
        }

        if (registrationDTO.getFirstName() == null || registrationDTO.getFirstName().isBlank()) {
            return new LogicResult<>("400", "The first name is required", null);
        }

        if (registrationDTO.getLastName() == null || registrationDTO.getLastName().isBlank()) {
            return new LogicResult<>("400", "The last name is required", null);
        }

        if (registrationDTO.getEmail() == null || registrationDTO.getEmail().isBlank()) {
            return new LogicResult<>("400", "The email is required", null);
        }

        return null;
    }

    /**
     * Validate the refusal reason.
     *
     * This method ensures that a refusal reason
     * is provided before rejecting a registration.
     *
     * @param refusalReason refusal reason
     * @return LogicResult containing an error if the reason is invalid,
     *         or null if it is valid
     */
    protected LogicResult<Void> validateRefusalReason(String refusalReason) {
        if (refusalReason == null || refusalReason.isBlank()) {
            return new LogicResult<>("400", "The refusal reason is required", null);
        }

        return null;
    }
}
