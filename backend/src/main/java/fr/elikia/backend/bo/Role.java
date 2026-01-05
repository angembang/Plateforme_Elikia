package fr.elikia.backend.bo;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(nullable = false)
    private String name;


    // ========================================================
    // Constructors
    // ========================================================

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }


    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getRoleId() {
        return roleId;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
