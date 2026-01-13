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
        String email = InputSanitizer.sanitize(dto.getEmail()).toLowerCase();

        /*
         Try to find the user first in the admin repository, then in the member repository
         If user is not found, return 401 Unauthorized
         */
        Admin admin = idaoAdmin.findByEmail(email);
        User user;
        if (admin != null) {
            user = admin;
        } else {
            Member member = idaoMember.findByEmail(email);
            if (member == null) {
                return new LogicResult<>("401", "Invalid credentials", null);
            }
            // Retrieve the actual user object from the Optional
            user = member;

            // ==========================================
            // Block connexion process if member not validated
            // ==========================================
            if(!"VALIDE".equals(member.getStatus())) {
                return new LogicResult<>("403", "Account not validate yet", null);
            }
        }

        // ==========================
        // Check for account lock
        // ==========================
        if (user.getLockUntil() != null &&
                user.getLockUntil().isAfter(LocalDateTime.now())) {

            // Account is temporarily locked; return 423 Locked
            return new LogicResult<>(
                    "423",
                    "Account temporarily locked. Please try again later.",
                    null
            );
        }

        // Password verification
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {

            // Increment failed login attempts
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);

            // Lock the account for 30 minutes if failed attempts exceed 3
            if (user.getFailedLoginAttempts() >= 3) {
                user.setLockUntil(LocalDateTime.now().plusMinutes(30));
            }

            // Save the updated user state (failed attempts and lock)
            if(user instanceof Admin) {
                idaoAdmin.update((Admin) user);
            } else {
                idaoMember.update((Member) user);
            }

            // Return 401 Unauthorized
            return new LogicResult<>("401", "Invalid credentials", null);
        }

        // ==========================
        // Successful login
        // ==========================

        // Reset failed login attempts and lock status
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
        // Save the updated user state (failed attempts and lock)
        if(user instanceof Admin) {
            idaoAdmin.update((Admin) user);
        } else {
            idaoMember.update((Member) user);
        }

        // Generate JWT token for the authenticated user
        String token = jwtService.generateToken(user);

        // Return success status with JWT token
        return new LogicResult<>("200", "Login successful", token);
    }


    /**
     * Registers a new member account.
     * Applies strong validation, sanitization, and password hashing.
     *
     * @param dto Registration data sent by the client
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> register(RegisterDTO dto) {

        LogicResult<Void> result =
                new LogicResult<>("400", "Validation error", null);
        boolean isValid = true;

        // ======================================
        // Sanitize user inputs (XSS protection)
        // =======================================
        String firstName = InputSanitizer.sanitize(dto.getFirstName());
        String lastName = InputSanitizer.sanitize(dto.getLastName());
        String password = InputSanitizer.sanitize(dto.getPassword());
        String email = dto.getEmail() != null
                ? dto.getEmail().trim().toLowerCase()
                : null;

        // Validation chain
        isValid &= isValidFirstName(firstName, result);
        isValid &= isValidLastName(lastName, result);
        isValid &= isValidEmail(email, result);
        isValid &= isEmailUnique(email, result);
        isValid &= isValidPassword(password, result);
        isValid &= isPasswordConfirmed(dto.getPassword(), dto.getConfirmPassword(), result);

        if (!isValid) {
            return result;
        }

        // ==========================
        // Password hashing
        // ==========================
        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        // Create member entity
        Member member = new Member();
        member.setFirstName(firstName);
        member.setLastName(lastName);
        member.setEmail(email);
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


}
