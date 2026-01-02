package fr.elikia.backend.bo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_registration")
public class EventRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrationId;

    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime registrationDate;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;


    // ========================================================
    // Constructors
    // ========================================================

    protected EventRegistration() {
    }

    public EventRegistration(String firstName, String lastName, String email, LocalDateTime registrationDate,
                             RegistrationStatus status, Event event, Member member) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.registrationDate = registrationDate;
        this.status = status;
        this.event = event;
        this.member = member;
    }


    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getRegistrationId() {
        return registrationId;
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

    public Event getEvent() {
        return event;
    }
    public void setEvent(Event event) {
        this.event = event;
    }

    public Member getMember() {
        return member;
    }
    public void setMember(Member member) {
        this.member = member;
    }
}
