package fr.elikia.backend.dto;
    /**
     * DTO utilisé pour transmettre le motif
     * de refus d'une demande d'adhésion.
     */
    public class RejectMembershipDTO {

        /**
         * Motif saisi par l'administrateur.
         */
        private String reason;

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
}
