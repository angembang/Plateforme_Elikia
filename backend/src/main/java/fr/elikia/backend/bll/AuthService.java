package fr.elikia.backend.bll;

import fr.elikia.backend.bo.*;
import fr.elikia.backend.dao.idao.IDAOAdmin;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.dao.idao.IDAORole;
import fr.elikia.backend.dto.LoginDTO;
import fr.elikia.backend.dto.RegisterDTO;
import fr.elikia.backend.security.InputSanitizer;
import fr.elikia.backend.security.jwt.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service responsible for handling authentication logic.
 * It manages login attempts, brute-force protection, password verification,
 * input validation, sanitization and JWT token generation for authenticated users.
 */
@Service
public class AuthService {
    private final IDAOMember idaoMember;
    private final IDAOAdmin idaoAdmin;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final IDAORole idaoRole;

    public AuthService(IDAOMember idaoMember, IDAOAdmin idaoAdmin,
                       PasswordEncoder passwordEncoder, JwtService jwtService, IDAORole idaoRole) {
        this.idaoMember = idaoMember;
        this.idaoAdmin = idaoAdmin;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.idaoRole = idaoRole;
    }


    /**
     * Handles user login by verifying credentials, account lock status,
     * and generates a JWT token upon successful authentication.
     *
     * @param dto Data Transfer Object containing login credentials (email and password)
     * @return LogicResult containing status code, message, and JWT token if login is successful
     */
    public LogicResult<String> login(LoginDTO dto) {

        // Sanitize user input email (XSS protection)
        String email = dto.getEmail() == null
                ? null
                : dto.getEmail().trim().toLowerCase();

        String rawPassword = dto.getPassword();

        /*
         Try to find the user first in the admin repository, then in the member repository
         If user is not found, return 401 Unauthorized
         */
        // Try to authenticate Admin first
        Admin admin = idaoAdmin.findByEmail(email);
        if (admin != null) {
            return authenticateUser(admin, rawPassword, "ADMIN");
        }

        // Then try to authenticate Member
        Member member = idaoMember.findByEmail(email);
        if (member != null) {
            return authenticateUser(member, rawPassword, "MEMBER");
        }

        // User not found
        return new LogicResult<>("401", "Invalid email or password", null);
    }

    /**
     * Handles authentication logic for any User (Admin or Member)
     */
    private LogicResult<String> authenticateUser(User user, String rawPassword, String role) {
        // ---- Check account lock
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
            return new LogicResult<>("423", "Account temporarily locked. Please try again later.", null);
        }

        // ---- Password verification
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= 3) {
                user.setLockUntil(LocalDateTime.now().plusMinutes(30));
            }
            saveUser(user);
            return new LogicResult<>("401", "Incorrect password", null);
        }

        // ---- Member-specific status check
        if (user instanceof Member member) {
            String status = member.getStatus();
            if (!"VALIDE".equals(status)) {
                String message = switch (status) {
                    case "INSCRIPTION_TRANSMISE" -> "Your membership is currently being processed.";
                    case "ANNULEE" -> "Your membership has been cancelled. Please contact support.";
                    default -> "Your membership is not active.";
                };
                return new LogicResult<>("403", message, null);
            }
        }

        // ---- Successful login
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
        saveUser(user);

        String token = jwtService.generateToken(user.getEmail(), role);
        return new LogicResult<>("200", role + " login successful", token);
    }

    /**
     * Saves a User entity (Admin or Member)
     */
    private void saveUser(User user) {
        if (user instanceof Admin admin) {
            idaoAdmin.update(admin);
        } else if (user instanceof Member member) {
            idaoMember.update(member);
        }
    }


    /**
     * Registers a new member account.
     * Applies strong validation, sanitization, and password hashing.
     *
     * @param dto Registration data sent by the client
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> register(RegisterDTO dto) {
        // Prepare a default validation error result
        LogicResult<Void> result = validationError();

        // Validate and sanitize register input
        AuthService.SanitizedRegisterInput input = validateAndSanitizeNews(dto, result);
        if (input == null) {
            // Validation failed, result already filled
            return result;
        }

        // ==========================
        // Password hashing
        // ==========================
        String hashedPassword = passwordEncoder.encode(input.password);

        // Create member entity
        Member member = new Member();
        member.setFirstName(input.firstName);
        member.setLastName(input.lastName);
        member.setEmail(input.email);
        member.setPassword(hashedPassword);
        member.setCreatedAt(LocalDate.now());

        // Membership rules
        member.setStatus("INSCRIPTION_TRANSMISE");
        member.setMembershipNumber(null);
        member.setMembershipDate(null);

        // member image
        member.setImage(null);

        // Assign default role (BENEVOLE)
        Role defaultRole = idaoRole.findByName("BENEVOLE");
        if (defaultRole == null) {
            return new LogicResult<>(
                    "500",
                    "Default role BENEVOLE not found. Please contact administrator.",
                    null
            );
        }
        member.setRole(defaultRole);

        // Security defaults
        member.setFailedLoginAttempts(0);
        member.setLockUntil(null);


        // Persist member
        idaoMember.create(member);

        return new LogicResult<>(
                "201",
                "Registration successful. Awaiting admin validation.",
                null
        );
    }

    // =========================================================
    // PRIVATE VALIDATION METHODS
    // =========================================================

    private boolean isValidFirstName(String firstName, LogicResult<?> result) {
        return isValidConditionFirstName(firstName, result);
    }

    static boolean isValidConditionFirstName(String firstName, LogicResult<?> result) {
        if (firstName == null || firstName.isBlank()) {
            result.setMessage("First name is required");
            return false;
        }
        if (firstName.length() < 2 || firstName.length() > 100) {
            result.setMessage("First name must be between 2 and 100 characters");
            return false;
        }
        return true;
    }

    private boolean isValidLastName(String lastName, LogicResult<?> result) {
        return isValidConditionLastName(lastName, result);
    }

    static boolean isValidConditionLastName(String lastName, LogicResult<?> result) {
        if (lastName == null || lastName.isBlank()) {
            result.setMessage("Last name is required");
            return false;
        }
        if (lastName.length() < 2 || lastName.length() > 100) {
            result.setMessage("Last name must be between 2 and 100 characters");
            return false;
        }
        return true;
    }

    private boolean isValidEmail(String email, LogicResult<?> result) {
        return isValidConditionEmail(email, result);
    }

    static boolean isValidConditionEmail(String email, LogicResult<?> result) {
        if (email == null || email.isBlank()) {
            result.setMessage("Email is required");
            return false;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            result.setMessage("Invalid email format");
            return false;
        }
        return true;
    }

    private boolean isEmailUnique(String email, LogicResult<?> result) {
        if ((idaoAdmin.findByEmail(email) != null)
                || (idaoMember.findByEmail(email) != null)) {
            result.setMessage("Email already in use");
            return false;
        }
        return true;
    }

    private boolean isValidPassword(String password, LogicResult<?> result) {
        return isValidConditionPassword(password, result);
    }

    static boolean isValidConditionPassword(String password, LogicResult<?> result) {
        if (password == null || password.isBlank()) {
            result.setMessage("Password is required");
            return false;
        }
        if (password.length() < 8) {
            result.setMessage("Password must contain at least 8 characters");
            return false;
        }
        return true;
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


    /**
     * Validates and sanitizes all fields of a RegisterDTO

     * This method centralizes:
     * - XSS sanitization
     * - Business validation rules

     * If a validation rule fails, the provided LogicResult is filled
     * and the method returns null.
     *
     * @param dto Input DTO
     * @param result Result object used to store validation error messages
     *
     * @return A SanitizedNewsInput object if valid, or null if validation fails
     */
    private AuthService.SanitizedRegisterInput validateAndSanitizeNews(
            RegisterDTO dto,
            LogicResult<?> result) {

        // Sanitize user inputs to prevent XSS attacks
        String firstName = InputSanitizer.sanitize(dto.getFirstName());
        String lastName = InputSanitizer.sanitize(dto.getLastName());
        String password = dto.getPassword();
        String confirmPassword = dto.getConfirmPassword();
        String email = dto.getEmail() != null
                ? dto.getEmail().trim().toLowerCase()
                : null;

        // Apply validation rules sequentially
        if (!isValidFirstName(firstName, result)) return null;
        if (!isValidLastName(lastName, result)) return null;
        if (!isValidEmail(email, result)) return null;
        if (!isEmailUnique(email, result)) return null;
        if (!isValidPassword(password, result)) return null;
        if (!isPasswordConfirmed(password, confirmPassword, result)) return null;

        // Return a container object with sanitized and validated values
        return new AuthService.SanitizedRegisterInput(
                firstName,
                lastName,
                password,
                confirmPassword,
                email
        );
    }


    /**
     * Immutable container for sanitized and validated Register input data

     * This record is used as an internal data structure between:
     * - the validation layer
     * - and the business logic of create/update operations

     * Responsibilities:
     * - Hold only trusted, sanitized values
     * - Prevent propagation of raw user input further in the service layer
     * - Guarantee that all fields have passed business validation rules

     * This object is never exposed outside the service layer
     */
    private record SanitizedRegisterInput(String firstName, String lastName,
                                      String email,
                                      String password,
                                      String confirmPassword) {
    }


    /**
     * Initial validation error
     */
    private <T> LogicResult<T> validationError() {

        return new LogicResult<>("400", "Validation error", null);
    }


}
