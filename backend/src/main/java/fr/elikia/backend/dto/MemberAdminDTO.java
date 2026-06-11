package fr.elikia.backend.dto;

import fr.elikia.backend.bo.Member;

import java.time.LocalDate;

public class MemberAdminDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDate createdAt;
    private String membershipNumber;
    private LocalDate membershipDate;
    private String status;
    private String image;
    private String roleName;

    public MemberAdminDTO(Member member) {
        this.userId = member.getUserId();
        this.firstName = member.getFirstName();
        this.lastName = member.getLastName();
        this.email = member.getEmail();
        this.createdAt = member.getCreatedAt();
        this.membershipNumber = member.getMembershipNumber();
        this.membershipDate = member.getMembershipDate();
        this.status = member.getStatus();
        this.image = member.getImage();
        this.roleName = member.getRole() != null ? member.getRole().getName() : null;
    }


    public String getFirstName() {
        return firstName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public String getMembershipNumber() {
        return membershipNumber;
    }

    public LocalDate getMembershipDate() {
        return membershipDate;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public String getRoleName() {
        return roleName;
    }
}