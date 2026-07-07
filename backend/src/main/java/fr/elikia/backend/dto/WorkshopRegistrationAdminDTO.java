package fr.elikia.backend.dto;

import fr.elikia.backend.bo.enums.RegistrationStatus;

import java.time.LocalDateTime;

/**
 * DTO used by administrators
 * to display workshop registrations.
 *
 * It avoids exposing the complete entity graph.
 */
public class WorkshopRegistrationAdminDTO {

    private Long registrationId;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDateTime registrationDate;

    private RegistrationStatus status;

    private Long workshopId;

    private String workshopTitle;

    private Long memberId;

    private String memberEmail;

    // ========================================================
    // Constructors
    // ========================================================

    public WorkshopRegistrationAdminDTO() {
    }

    public WorkshopRegistrationAdminDTO(
            Long registrationId,
            String firstName,
            String lastName,
            String email,
            LocalDateTime registrationDate,
            RegistrationStatus status,
            Long workshopId,
            String workshopTitle,
            Long memberId,
            String memberEmail
    ) {
        this.registrationId = registrationId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationDate = registrationDate;
        this.status = status;
        this.workshopId = workshopId;
        this.workshopTitle = workshopTitle;
        this.memberId = memberId;
        this.memberEmail = memberEmail;
    }

    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
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

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }

    public Long getWorkshopId() {
        return workshopId;
    }

    public void setWorkshopId(Long workshopId) {
        this.workshopId = workshopId;
    }

    public String getWorkshopTitle() {
        return workshopTitle;
    }

    public void setWorkshopTitle(String workshopTitle) {
        this.workshopTitle = workshopTitle;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }
}