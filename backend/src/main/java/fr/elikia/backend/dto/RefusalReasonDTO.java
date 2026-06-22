package fr.elikia.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilisé pour récupérer le motif d'un refus.
 *
 * Ce DTO peut être réutilisé pour plusieurs types de demandes :
 * - refus d'une inscription à un événement
 * - refus d'une inscription à un atelier
 */
public class RefusalReasonDTO {

    @NotBlank(message = "The refusal reason is required")
    private String reason;

    public RefusalReasonDTO() {
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}