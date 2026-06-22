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
     * @param to adresse email du membre
     * @param firstName prénom du membre
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
                        "Vous pouvez désormais accéder à votre espace membre avec l'adresse email utilisée lors de votre inscription : " + to + ".\n" +
                        "Le mot de passe est celui que vous avez choisi lors de votre inscription.\n\n" +
                        "Cordialement,\n" +
                        "Association Elikia"
        );

        mailSender.send(message);
    }

    /**
     * Envoie un email lorsqu'une demande d'adhésion est refusée.
     *
     * @param to adresse email du membre
     * @param firstName prénom du membre
     * @param reason motif du refus saisi par l'administrateur
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
}