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

import java.time.LocalDateTime;

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

    public EventRegistrationService(
            IDAOEvent idaoEvent,
            IDAOEventRegistration idaoEventRegistration,
            IDAOMember idaoMember
    ) {
        this.idaoEvent = idaoEvent;
        this.idaoEventRegistration = idaoEventRegistration;
        this.idaoMember = idaoMember;
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
}
