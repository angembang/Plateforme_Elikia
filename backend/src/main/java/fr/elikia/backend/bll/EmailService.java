package fr.elikia.backend.bll;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsable de l'envoi des emails automatiques
 * liés aux demandes d'adhésion.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Envoie un email lorsqu'une demande d'adhésion est acceptée.
     *
     * @param to               adresse email du membre
     * @param firstName        prénom du membre
     * @param membershipNumber numéro d'adhésion généré
     */
    public void sendMembershipAcceptedEmail(
            String to,
            String firstName,
            String membershipNumber
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre demande d'adhésion a été acceptée");
        message.setText(
                "Bonjour " + firstName + ",\n\n" +
                        "Votre demande d'adhésion à l'association Elikia a été acceptée.\n" +
                        "Votre numéro d'adhésion est : " + membershipNumber + ".\n\n" +
                        "Vous pouvez désormais accéder à votre espace membre.\n\n" +
                        "Cordialement,\n" +
                        "Association Elikia"
        );

        mailSender.send(message);
    }

    /**
     * Envoie un email lorsqu'une demande d'adhésion est refusée.
     *
     * @param to        adresse email du membre
     * @param firstName prénom du membre
     * @param reason    motif du refus saisi par l'administrateur
     */
    public void sendMembershipRejectedEmail(
            String to,
            String firstName,
            String reason
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre demande d'adhésion a été refusée");
        message.setText(
                "Bonjour " + firstName + ",\n\n" +
                        "Votre demande d'adhésion à l'association Elikia a été refusée.\n\n" +
                        "Motif du refus : " + reason + "\n\n" +
                        "Cordialement,\n" +
                        "Association Elikia"
        );

        mailSender.send(message);
    }

    /**
     * Envoie un email lorsqu'une inscription à un événement est acceptée.
     *
     * @param to         adresse email du participant
     * @param firstName  prénom du participant
     * @param eventTitle titre de l'événement
     */
    public void sendEventRegistrationAcceptedEmail(
            String to,
            String firstName,
            String eventTitle
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre inscription à l'événement a été acceptée");
        message.setText(
                "Bonjour " + firstName + ",\n\n" +
                        "Votre inscription à l'événement \"" + eventTitle + "\" a été acceptée.\n\n" +
                        "Nous vous remercions pour votre inscription.\n\n" +
                        "Cordialement,\n" +
                        "Association Elikia"
        );

        mailSender.send(message);
    }

    /**
     * Envoie un email lorsqu'une inscription à un événement est refusée.
     *
     * @param to         adresse email du participant
     * @param firstName  prénom du participant
     * @param eventTitle titre de l'événement
     * @param reason     motif du refus saisi par l'administrateur
     */
    public void sendEventRegistrationRejectedEmail(
            String to,
            String firstName,
            String eventTitle,
            String reason
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre inscription à l'événement a été refusée");
        message.setText(
                "Bonjour " + firstName + ",\n\n" +
                        "Votre inscription à l'événement \"" + eventTitle + "\" a été refusée.\n\n" +
                        "Motif du refus : " + reason + "\n\n" +
                        "Cordialement,\n" +
                        "Association Elikia"
        );

        mailSender.send(message);
    }

    /**
     * Send an email when a workshop registration is approved.
     *
     * @param to            participant email address
     * @param firstName     participant first name
     * @param workshopTitle workshop title
     */
    public void sendWorkshopRegistrationAcceptedEmail(
            String to,
            String firstName,
            String workshopTitle
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre inscription à l'atelier a été acceptée");
        message.setText(
                "Bonjour " + firstName + ",\n\n" +
                        "Votre inscription à l'atelier \"" + workshopTitle + "\" a été acceptée.\n\n" +
                        "Nous vous remercions pour votre inscription.\n\n" +
                        "Cordialement,\n" +
                        "Association Elikia"
        );

        mailSender.send(message);
    }

    /**
     * Send an email when a workshop registration is rejected.
     *
     * @param to            participant email address
     * @param firstName     participant first name
     * @param workshopTitle workshop title
     * @param reason        refusal reason
     */
    public void sendWorkshopRegistrationRejectedEmail(
            String to,
            String firstName,
            String workshopTitle,
            String reason
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Votre inscription à l'atelier a été refusée");
        message.setText(
                "Bonjour " + firstName + ",\n\n" +
                        "Votre inscription à l'atelier \"" + workshopTitle + "\" a été refusée.\n\n" +
                        "Motif du refus : " + reason + "\n\n" +
                        "Cordialement,\n" +
                        "Association Elikia"
        );

        mailSender.send(message);
    }
}