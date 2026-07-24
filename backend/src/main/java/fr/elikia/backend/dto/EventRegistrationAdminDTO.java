package fr.elikia.backend.dto;

import fr.elikia.backend.bo.enums.RegistrationStatus;

import java.time.LocalDateTime;

/**
 * DTO utilisé pour retourner les inscriptions d'un événement
 * dans l'espace administrateur.
 * Il évite de retourner les entités Event et Member complètes
 * afin d'empêcher les références circulaires.
 */
public class EventRegistrationAdminDTO {
    private Long registrationId;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime registrationDate;
    private RegistrationStatus status;

    private Long eventId;
    private String eventTitle;

    private Long memberId;
    private String memberEmail;

    public EventRegistrationAdminDTO() {
    }

    public EventRegistrationAdminDTO(
            Long registrationId,
            String firstName,
            String lastName,
            String email,
            LocalDateTime registrationDate,
            RegistrationStatus status,
            Long eventId,
            String eventTitle,
            Long memberId,
            String memberEmail
    ) {
        this.registrationId = registrationId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationDate = registrationDate;
        this.status = status;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.memberId = memberId;
        this.memberEmail = memberEmail;
    }

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

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
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