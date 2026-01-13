package fr.elikia.backend.bo;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long userId;

    @Column(nullable = false)
    protected String firstName;

    @Column(nullable = false)
    protected String lastName;

    @Column(nullable = false, unique = true)
    protected String email;

    @Column(nullable = false)
    protected String password;

    @Column(nullable = false)
    protected LocalDate createdAt;

    // Security login
    protected int failedLoginAttempts = 0;
    protected LocalDateTime lockUntil;


    // ========================================================
    // Constructors
    // ========================================================

    protected User() {
    }

    protected User(String firstName, String lastName, String email, String password, LocalDate createdAt, int failedLoginAttempts, LocalDateTime lockUntil) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
        this.failedLoginAttempts = failedLoginAttempts;
        this.lockUntil = lockUntil;
    }


    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getUserId() {
        return userId;
    }

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

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public int getFailedLoginAttempts() {
        return failedLoginAttempts;
    }
    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public LocalDateTime getLockUntil() {
        return lockUntil;
    }
    public void setLockUntil(LocalDateTime lockUntil) {
        this.lockUntil = lockUntil;
    }
}