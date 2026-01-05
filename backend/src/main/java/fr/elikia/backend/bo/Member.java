package fr.elikia.backend.bo;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "member")
public class Member extends User {
    @Column(nullable = false)
    private String membershipNumber;

    @Column(nullable = false)
    private LocalDate membershipDate;

    @Column(nullable = false)
    private String status;

    private String image;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;


    // ========================================================
    // Constructors
    // ========================================================
    public Member() {
    }

    public Member(String firstName, String lastName, String email, String password, LocalDate createdAt,
                  String membershipNumber, LocalDate membershipDate, String status, String image, Role role) {
        super(firstName, lastName, email, password, createdAt);
        this.membershipNumber = membershipNumber;
        this.membershipDate = membershipDate;
        this.status = status;
        this.image = image;
        this.role = role;
    }


    // ========================================================
    // Getters & Setters
    // ========================================================

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public LocalDate getMembershipDate() {
        return membershipDate;
    }
    public void setMembershipDate(LocalDate membershipDate) {
        this.membershipDate = membershipDate;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }
}
