package fr.elikia.backend.bo;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "admin")
public class Admin extends User {

    // ========================================================
    // Constructors
    // ========================================================

    public Admin() {
    }

    public Admin(String firstName, String lastName, String email, String password, LocalDate createdAt) {
        super(firstName, lastName, email, password, createdAt);
    }

}
