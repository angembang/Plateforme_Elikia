package fr.elikia.backend.bo;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "donation")
public class Donation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donationId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime donationDate;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, unique = true)
    private String paymentReference;

    private String firstName;

    private String lastName;

    private String email;

    @Column(nullable = false)
    private boolean anonymous;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = true)
    private Member member;


    // ========================================================
    // Constructors
    // ========================================================

    public Donation() {
    }

    public Donation(Double amount, LocalDateTime donationDate, String status,
                    String paymentReference, String firstName, String lastName, String email,
                    boolean anonymous, Member member) {
        this.amount = amount;
        this.donationDate = donationDate;
        this.status = status;
        this.paymentReference = paymentReference;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.anonymous = anonymous;
        this.member = member;
    }


    // ========================================================
    // Getters & Setters
    // ========================================================

    public Long getDonationId() {
        return donationId;
    }

    public Double getAmount() {
        return amount;
    }
    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDateTime getDonationDate() {
        return donationDate;
    }
    public void setDonationDate(LocalDateTime donationDate) {
        this.donationDate = donationDate;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
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

    public boolean isAnonymous() {
        return anonymous;
    }
    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public Member getMember() {
        return member;
    }
    public void setMember(Member member) {
        this.member = member;
    }
}
