package fr.elikia.backend.bll;

import fr.elikia.backend.bo.*;
import fr.elikia.backend.dao.idao.IDAOAdmin;
import fr.elikia.backend.dto.RegisterDTO;
import fr.elikia.backend.security.InputSanitizer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static fr.elikia.backend.bll.AuthService.*;

@Service
public class AdminService {
    private final IDAOAdmin idaoAdmin;
    private final PasswordEncoder passwordEncoder;

    public AdminService(IDAOAdmin idaoAdmin,
                       PasswordEncoder passwordEncoder) {
        this.idaoAdmin = idaoAdmin;
        this.passwordEncoder = passwordEncoder;
    }


    /**
     * Registers a new admin account
     * Applies strong validation, sanitization, and password hashing.
     *
     * @param registerDTO Registration data sent by the client
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> createAdmin(RegisterDTO registerDTO) {

        // Prepare a default validation error result
        LogicResult<Void> result =validationError();

        // Validate and sanitize all inputs in a single step
        AdminService.SanitizedAdminInput input = validateAndSanitizeAdmin(registerDTO, result);
        if (input == null) {
            // Validation failed, result already contains the error message
            return result;
        }


        // hash the password
        String hashedPassword = passwordEncoder.encode(input.password);

        // Create member entity
        Admin admin = new Admin();
        admin .setFirstName(input.firstName);
        admin .setLastName(input.lastName);
        admin .setEmail(input.email);
        admin .setPassword(hashedPassword);
        admin .setCreatedAt(LocalDate.now());

        // Persist admin
        idaoAdmin.create(admin);

        return new LogicResult<>(
                "201",
                "Registration successful.",
                null
        );
    }

    // =========================================================
    // Private validation methods
    // =========================================================

    private boolean isValidFirstName(String firstName, LogicResult<?> result) {
        return isValidConditionFirstName(firstName, result);
    }

    private boolean isValidLastName(String lastName, LogicResult<?> result) {
        return isValidConditionLastName(lastName, result);
    }

    private boolean isValidEmail(String email, LogicResult<?> result) {
        return AuthService.isValidConditionEmail(email, result);
    }

    private boolean isEmailUnique(String email, LogicResult<?> result) {
        if (idaoAdmin.findByEmail(email) != null) {
            result.setMessage("Email already in use");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, LogicResult<?> result) {
        return AuthService.isValidConditionPassword(password, result);
    }

    private boolean isPasswordConfirmed(String password,
                                        String confirm,
                                        LogicResult<?> result) {
        if (!password.equals(confirm)) {
            result.setMessage("Passwords do not match");
            return false;
        }
        return true;
    }


    // =========================================================
    // Helper methods
    // =========================================================
    /**
     * Initial validation error
     */
    private <T> LogicResult<T> validationError() {

        return new LogicResult<>("400", "Validation error", null);
    }


    /**
     * Validates and sanitizes all fields of a registerDTO

     * This method centralizes:
     * - XSS sanitization
     * - Business validation rules

     * If a validation rule fails, the provided LogicResult is filled
     * and the method returns null.
     *
     * @param registerDTO Input DTO
     * @param result Result object used to store validation error messages
     *
     * @return A SanitizedAdminInput object if valid, or null if validation fails
     */
    private AdminService.SanitizedAdminInput validateAndSanitizeAdmin(
            RegisterDTO registerDTO,
            LogicResult<?> result) {

        // Sanitize user inputs to prevent XSS attacks
        String firstName = InputSanitizer.sanitize(registerDTO.getFirstName());
        String lastName = InputSanitizer.sanitize(registerDTO.getLastName());

        // Non-string fields do not require sanitization
        String password = registerDTO.getPassword();
        String confirmPassword = registerDTO.getConfirmPassword();
        String email = registerDTO.getEmail() != null
                ? registerDTO.getEmail().trim().toLowerCase()
                : null;

        // Apply validation rules sequentially
        if (!isValidFirstName(firstName, result)) return null;
        if (!isValidLastName(lastName, result)) return null;
        if (!isValidEmail(email, result)) return null;
        if (!isEmailUnique(email, result)) return null;
        if (!isValidPassword(password, result)) return null;
        if (!isPasswordConfirmed(password, confirmPassword, result)) return null;

        // Return a container object with sanitized and validated values
        return new SanitizedAdminInput(
                firstName,
                lastName,
                email,
                password,
                confirmPassword
        );
    }


    /**
     * Immutable container for sanitized and validated Admin input data

     * This record is used as an internal data structure between:
     * - the validation layer
     * - and the business logic of create/update operations

     * Responsibilities:
     * - Hold only trusted, sanitized values
     * - Prevent propagation of raw user input further in the service layer
     * - Guarantee that all fields have passed business validation rules

     * This object is never exposed outside the service layer
     */
    private record SanitizedAdminInput(String firstName, String lastName,
                                      String email, String password,
                                      String confirmPassword) {
    }


    /**
     * Update admin
     */
    public LogicResult<Admin> updateAdmin(Long id) {
        Admin admin = idaoAdmin.findById(id);
        if (admin == null) {
            return new LogicResult<>("404", "Admin not found", null);
        }

        return new LogicResult<>("200", "Member updated", idaoAdmin.update(admin));
    }

    /**
     * Delete member
     */
    public LogicResult<Void> delete(Long id) {
        Admin admin = idaoAdmin.findById(id);
        if(admin == null) {
            return new LogicResult<>("404", "Admin not found", null);
        }
        Long adminId = admin.getUserId();
        if (adminId == null) {
            return new LogicResult<>("404", "Admin not found", null);
        }
        boolean isDeleted = idaoAdmin.deleteById(adminId);
        if(isDeleted) {
            return new LogicResult<>("200", "Admin deleted", null);

        }
        return new LogicResult<>("404", "A error occurs during the deleting", null);


    }


}
