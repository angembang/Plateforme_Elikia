package fr.elikia.backend.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO utilisé pour transmettre le motif
 * de refus saisi par l'administrateur
 * lors du traitement d'une demande d'adhésion.
 */
    public class RejectMembershipDTO {

        /**
         * Motif saisi par l'administrateur.
         */
        @NotBlank(message = "Le motif du refus est obligatoire")
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
}
