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
import fr.elikia.backend.dto.EventRegistrationAdminDTO;
import fr.elikia.backend.dto.EventRegistrationDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for managing event registration business logic.
 *
 * This service handles:
 * - registration validation
 * - event existence verification
 * - duplicate registration prevention
 * - member-only event access control
 * - registration approval, rejection and cancellation
 */
@Service
public class EventRegistrationService extends AbstractRegistrationService {

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
     * Create an event registration for a visitor or a member.
     *
     * Business rules:
     * - a visitor can register only to PUBLIC events
     * - a member can register to PUBLIC and MEMBER_ONLY events
     * - every new registration is created with PENDING status
     *
     * @param eventId event identifier
     * @param eventRegistrationDTO registration data
     * @param memberEmail connected member email, null for visitors
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> registerToEvent(
            Long eventId,
            EventRegistrationDTO eventRegistrationDTO,
            String memberEmail
    ) {
        LogicResult<Void> validationResult = validateActivityId(eventId);
        if (validationResult != null) {
            return validationResult;
        }

        // Retrieve the existing event
        Event event = idaoEvent.findById(eventId);

        if (event == null) {
            return new LogicResult<>("404", "Event not found", null);
        }

        validationResult = validateRegistrationData(eventRegistrationDTO);
        if (validationResult != null) {
            return validationResult;
        }

        String firstName = eventRegistrationDTO.getFirstName();
        String lastName = eventRegistrationDTO.getLastName();
        String email = eventRegistrationDTO.getEmail();

        Member member = null;

        if (memberEmail != null && !memberEmail.isBlank()) {

            // Retrieve the authenticated member
            member = idaoMember.findByEmail(memberEmail);

            if (member == null) {
                return new LogicResult<>("404", "Member not found", null);
            }

            // Check whether the member is already registered
            if (idaoEventRegistration.existsByEventAndMember(event, member)) {
                return new LogicResult<>(
                        "400",
                        "This member is already registered for this event",
                        null
                );
            }
        } else {
            // Check whether the visitor is already registered
            if (idaoEventRegistration.existsByEventAndEmail(event, email)) {
                return new LogicResult<>(
                        "400",
                        "This email is already registered for this event",
                        null
                );
            }
        }

        // Prevent visitors from registering for member-only events
        if (event.getVisibility() == Visibility.MEMBER_ONLY && member == null) {
            return new LogicResult<>(
                    "403",
                    "This event is reserved for members only",
                    null
            );
        }

        // Create the registration entity
        EventRegistration eventRegistration = new EventRegistration();

        eventRegistration.setFirstName(firstName);
        eventRegistration.setLastName(lastName);
        eventRegistration.setEmail(email);
        eventRegistration.setRegistrationDate(LocalDateTime.now());
        eventRegistration.setStatus(RegistrationStatus.PENDING);
        eventRegistration.setEvent(event);
        eventRegistration.setMember(member);

        // Save the registration
        EventRegistration savedRegistration =
                idaoEventRegistration.create(eventRegistration);

        if (savedRegistration == null) {
            return new LogicResult<>(
                    "500",
                    "Failed to create event registration",
                    null
            );
        }

        // Return a successful result
        return new LogicResult<>(
                "201",
                "Event registration created successfully",
                null
        );
    }

    /**
     * Convert an event registration entity to an admin DTO.
     *
     * @param registration event registration entity
     * @return EventRegistrationAdminDTO
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
     * Retrieve all registrations linked to a specific event.
     *
     * @param eventId event identifier
     * @return LogicResult containing event registrations for admin display
     */
    public LogicResult<List<EventRegistrationAdminDTO>> getRegistrationsByEvent(Long eventId) {
        LogicResult<Void> validationResult = validateActivityId(eventId);
        if (validationResult != null) {
            return new LogicResult<>(
                    validationResult.getCode(),
                    validationResult.getMessage(),
                    null
            );
        }
        // Retrieve the existing event
        Event event = idaoEvent.findById(eventId);

        if (event == null) {
            return new LogicResult<>("404", "Event not found", null);
        }

        // Retrieve registrations linked to the event
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
     * Approve an event registration.
     *
     * @param registrationId registration identifier
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> approveRegistration(Long registrationId) {
        LogicResult<Void> validationResult = validateRegistrationId(registrationId);
        if (validationResult != null) {
            return validationResult;
        }


        // Retrieve the existing registration
        EventRegistration registration =
                idaoEventRegistration.findById(registrationId);

        if (registration == null) {
            return new LogicResult<>("404", "Event registration not found", null);
        }

        // Update the registration status
        registration.setStatus(RegistrationStatus.APPROVED);

        // Save the updated registration
        EventRegistration updatedRegistration =
                idaoEventRegistration.update(registration);

        if (updatedRegistration == null) {
            return new LogicResult<>("500", "Failed to approve event registration", null);
        }

        // Send the acceptance confirmation email
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
     * Reject an event registration.
     *
     * @param registrationId registration identifier
     * @param refusalReason refusal reason
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> rejectRegistration(Long registrationId, String refusalReason) {
        LogicResult<Void> validationResult = validateRegistrationId(registrationId);
        if (validationResult != null) {
            return validationResult;
        }

        validationResult = validateRefusalReason(refusalReason);
        if (validationResult != null) {
            return validationResult;
        }

        // Retrieve the existing registration
        EventRegistration registration =
                idaoEventRegistration.findById(registrationId);

        if (registration == null) {
            return new LogicResult<>("404", "Event registration not found", null);
        }

        // Update the registration status
        registration.setStatus(RegistrationStatus.REJECTED);

        // Save the updated registration
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
     * Cancel an event registration.
     *
     * @param registrationId registration identifier
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> cancelRegistration(Long registrationId) {
        LogicResult<Void> validationResult = validateRegistrationId(registrationId);
        if (validationResult != null) {
            return validationResult;
        }

        // Retrieve the existing registration
        EventRegistration registration =
                idaoEventRegistration.findById(registrationId);

        if (registration == null) {
            return new LogicResult<>("404", "Event registration not found", null);
        }

        // Update the registration status
        registration.setStatus(RegistrationStatus.CANCELLED);

        // Save the updated registration
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
