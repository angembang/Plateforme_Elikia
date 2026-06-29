package fr.elikia.backend.bll;

import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.EventRegistration;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.enums.RegistrationStatus;
import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.dao.idao.IDAOEvent;
import fr.elikia.backend.dao.idao.IDAOEventRegistration;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.dto.EventRegistrationDTO;
import org.springframework.stereotype.Service;
import fr.elikia.backend.dto.EventRegistrationAdminDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsable de la gestion de la logique métier
 * liée aux inscriptions aux événements.
 *
 * Ce service gère :
 * - la validation des données d'inscription
 * - la vérification de l'existence de l'événement
 * - la prévention des inscriptions en double
 * - le contrôle d'accès aux événements réservés aux membres
 * - la création d'une inscription avec le statut PENDING
 */
@Service
public class EventRegistrationService {

    private final IDAOEvent idaoEvent;
    private final IDAOEventRegistration idaoEventRegistration;
    private final IDAOMember idaoMember;
    private final EmailService emailService;

    public EventRegistrationService(
            IDAOEvent idaoEvent,
            IDAOEventRegistration idaoEventRegistration,
            IDAOMember idaoMember,
            EmailService emailService
    ) {
        this.idaoEvent = idaoEvent;
        this.idaoEventRegistration = idaoEventRegistration;
        this.idaoMember = idaoMember;
        this.emailService = emailService;
    }

    /**
     * Crée une inscription à un événement pour un visiteur ou un membre.
     *
     * Règles métier :
     * - Un visiteur peut s'inscrire uniquement aux événements PUBLIC
     * - Un membre peut s'inscrire aux événements PUBLIC et MEMBER_ONLY
     * - Toute nouvelle inscription est créée avec le statut PENDING
     *
     * @param eventId identifiant de l'événement
     * @param eventRegistrationDTO données nécessaires à l'inscription
     * @param memberEmail email du membre connecté, null pour un visiteur
     *
     * @return LogicResult indiquant le succès ou l'échec de l'opération
     */
    public LogicResult<Void> registerToEvent(
            Long eventId,
            EventRegistrationDTO eventRegistrationDTO,
            String memberEmail
    ) {
        // Valider l'identifiant
        if (eventId == null || eventId <= 0) {
            return new LogicResult<>("400", "The event identifier is required", null);
        }

        // Récupérer l'événement existant
        Event event = idaoEvent.findById(eventId);

        if (event == null) {
            return new LogicResult<>("404", "Event not found", null);
        }

        // Valider les données d'inscription
        if (eventRegistrationDTO == null) {
            return new LogicResult<>("400", "Registration data is required", null);
        }

        String firstName = eventRegistrationDTO.getFirstName();
        String lastName = eventRegistrationDTO.getLastName();
        String email = eventRegistrationDTO.getEmail();

        if (firstName == null || firstName.isBlank()) {
            return new LogicResult<>("400", "The first name is required", null);
        }

        if (lastName == null || lastName.isBlank()) {
            return new LogicResult<>("400", "The last name is required", null);
        }

        if (email == null || email.isBlank()) {
            return new LogicResult<>("400", "The email is required", null);
        }

        Member member = null;

        if (memberEmail != null && !memberEmail.isBlank()) {

            // Récupérer le membre connecté
            member = idaoMember.findByEmail(memberEmail);

            if (member == null) {
                return new LogicResult<>("404", "Member not found", null);
            }

            // Vérifier si le membre est déjà inscrit
            if (idaoEventRegistration.existsByEventAndMember(event, member)) {
                return new LogicResult<>(
                        "400",
                        "This member is already registered for this event",
                        null
                );
            }
        } else {
            // Vérifier si le visiteur est déjà inscrit
            if (idaoEventRegistration.existsByEventAndEmail(event, email)) {
                return new LogicResult<>(
                        "400",
                        "This email is already registered for this event",
                        null
                );
            }
        }

        // Bloquer les visiteurs pour les événements réservés aux membres
        if (event.getVisibility() == Visibility.MEMBER_ONLY && member == null) {
            return new LogicResult<>(
                    "403",
                    "This event is reserved for members only",
                    null
            );
        }

        // Créer l'entité d'inscription
        EventRegistration eventRegistration = new EventRegistration();

        eventRegistration.setFirstName(firstName);
        eventRegistration.setLastName(lastName);
        eventRegistration.setEmail(email);
        eventRegistration.setRegistrationDate(LocalDateTime.now());
        eventRegistration.setStatus(RegistrationStatus.PENDING);
        eventRegistration.setEvent(event);
        eventRegistration.setMember(member);

        // Enregistrer l'inscription
        EventRegistration savedRegistration =
                idaoEventRegistration.create(eventRegistration);

        if (savedRegistration == null) {
            return new LogicResult<>(
                    "500",
                    "Failed to create event registration",
                    null
            );
        }

        // Retourner le succès
        return new LogicResult<>(
                "201",
                "Event registration created successfully",
                null
        );
    }

    /**
     * Convertit une inscription à un événement en DTO pour l'administration.
     */
    private EventRegistrationAdminDTO mapToEventRegistrationAdminDTO(
            EventRegistration registration
    ) {
        Long memberId = null;
        String memberEmail = null;

        if (registration.getMember() != null) {
            memberId = registration.getMember().getUserId();
            memberEmail = registration.getMember().getEmail();
        }

        return new EventRegistrationAdminDTO(
                registration.getRegistrationId(),
                registration.getFirstName(),
                registration.getLastName(),
                registration.getEmail(),
                registration.getRegistrationDate(),
                registration.getStatus(),
                registration.getEvent().getEventId(),
                registration.getEvent().getTitle(),
                memberId,
                memberEmail
        );
    }

    /**
     * Récupère toutes les inscriptions liées à un événement donné.
     *
     * Règles métier :
     * - vérifier que l'identifiant de l'événement est valide
     * - vérifier que l'événement existe
     * - retourner la liste des inscriptions associées à cet événement
     *
     * @param eventId identifiant de l'événement
     * @return LogicResult contenant la liste des inscriptions
     */
    public LogicResult<List<EventRegistrationAdminDTO>> getRegistrationsByEvent(Long eventId) {
        // Valider l'identifiant de l'événement
        if (eventId == null || eventId <= 0) {
            return new LogicResult<>("400", "The event identifier is required", null);
        }

        // Récupérer l'événement existant
        Event event = idaoEvent.findById(eventId);

        if (event == null) {
            return new LogicResult<>("404", "Event not found", null);
        }

        // Récupérer les inscriptions liées à cet événement
        List<EventRegistration> registrations =
                idaoEventRegistration.findByEvent(event);

        List<EventRegistrationAdminDTO> registrationDTOs =
                registrations.stream()
                        .map(this::mapToEventRegistrationAdminDTO)
                        .toList();

        return new LogicResult<>(
                "200",
                "Event registrations retrieved successfully",
                registrationDTOs
        );
    }

    /**
     * Accepte une inscription à un événement.
     *
     * Règles métier :
     * - vérifier que l'inscription existe
     * - changer le statut de l'inscription en APPROVED
     * - enregistrer la modification
     *
     * @param registrationId identifiant de l'inscription
     * @return LogicResult indiquant le résultat de l'opération
     */
    public LogicResult<Void> approveRegistration(Long registrationId) {
        // Valider l'identifiant de l'inscription
        if (registrationId == null || registrationId <= 0) {
            return new LogicResult<>("400", "The registration identifier is required", null);
        }

        // Récupérer l'inscription existante
        EventRegistration registration =
                idaoEventRegistration.findById(registrationId);

        if (registration == null) {
            return new LogicResult<>("404", "Event registration not found", null);
        }

        // Mettre à jour le statut de l'inscription
        registration.setStatus(RegistrationStatus.APPROVED);

        // Enregistrer la modification
        EventRegistration updatedRegistration =
                idaoEventRegistration.update(registration);

        if (updatedRegistration == null) {
            return new LogicResult<>("500", "Failed to approve event registration", null);
        }

        // Envoyer un email de confirmation après l'acceptation
        emailService.sendEventRegistrationAcceptedEmail(
                registration.getEmail(),
                registration.getFirstName(),
                registration.getEvent().getTitle()
        );

        return new LogicResult<>(
                "200",
                "Event registration approved successfully",
                null
        );
    }

    /**
     * Refuse une inscription à un événement.
     *
     * Règles métier :
     * - vérifier que l'inscription existe
     * - vérifier que le motif du refus est renseigné
     * - changer le statut de l'inscription en REJECTED
     * - enregistrer la modification
     *
     * Remarque :
     * Le motif du refus n'est pas enregistré en base de données.
     * Il pourra être utilisé plus tard pour l'envoi de l'email de refus.
     *
     * @param registrationId identifiant de l'inscription
     * @param refusalReason motif du refus
     * @return LogicResult indiquant le résultat de l'opération
     */
    public LogicResult<Void> rejectRegistration(Long registrationId, String refusalReason) {
        // Valider l'identifiant de l'inscription
        if (registrationId == null || registrationId <= 0) {
            return new LogicResult<>("400", "The registration identifier is required", null);
        }

        // Valider le motif du refus
        if (refusalReason == null || refusalReason.isBlank()) {
            return new LogicResult<>("400", "The refusal reason is required", null);
        }

        // Récupérer l'inscription existante
        EventRegistration registration =
                idaoEventRegistration.findById(registrationId);

        if (registration == null) {
            return new LogicResult<>("404", "Event registration not found", null);
        }

        // Mettre à jour le statut de l'inscription
        registration.setStatus(RegistrationStatus.REJECTED);

        // Enregistrer la modification
        EventRegistration updatedRegistration =
                idaoEventRegistration.update(registration);

        if (updatedRegistration == null) {
            return new LogicResult<>("500", "Failed to reject event registration", null);
        }

        emailService.sendEventRegistrationRejectedEmail(
                registration.getEmail(),
                registration.getFirstName(),
                registration.getEvent().getTitle(),
                refusalReason
        );

        return new LogicResult<>(
                "200",
                "Event registration rejected successfully",
                null
        );
    }

    /**
     * Annule une inscription à un événement.
     *
     * Règles métier :
     * - vérifier que l'inscription existe
     * - changer le statut de l'inscription en CANCELLED
     * - enregistrer la modification
     *
     * @param registrationId identifiant de l'inscription
     * @return LogicResult indiquant le résultat de l'opération
     */
    public LogicResult<Void> cancelRegistration(Long registrationId) {
        // Valider l'identifiant de l'inscription
        if (registrationId == null || registrationId <= 0) {
            return new LogicResult<>("400", "The registration identifier is required", null);
        }

        // Récupérer l'inscription existante
        EventRegistration registration =
                idaoEventRegistration.findById(registrationId);

        if (registration == null) {
            return new LogicResult<>("404", "Event registration not found", null);
        }

        // Mettre à jour le statut de l'inscription
        registration.setStatus(RegistrationStatus.CANCELLED);

        // Enregistrer la modification
        EventRegistration updatedRegistration =
                idaoEventRegistration.update(registration);

        if (updatedRegistration == null) {
            return new LogicResult<>("500", "Failed to cancel event registration", null);
        }

        return new LogicResult<>(
                "200",
                "Event registration cancelled successfully",
                null
        );
    }
}
