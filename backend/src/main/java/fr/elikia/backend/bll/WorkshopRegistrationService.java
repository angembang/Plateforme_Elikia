package fr.elikia.backend.bll;

import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.Workshop;
import fr.elikia.backend.bo.WorkshopRegistration;
import fr.elikia.backend.bo.enums.RegistrationStatus;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.dao.idao.IDAOWorkshop;
import fr.elikia.backend.dao.idao.IDAOWorkshopRegistration;
import fr.elikia.backend.dto.WorkshopRegistrationAdminDTO;
import fr.elikia.backend.dto.WorkshopRegistrationDTO;
import org.springframework.stereotype.Service;
import fr.elikia.backend.bo.enums.Visibility;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for managing workshop registration business logic.
 * This service handles:
 * - registration validation
 * - workshop existence verification
 * - duplicate registration prevention
 * - registration approval, rejection and cancellation
 */
@Service
public class WorkshopRegistrationService extends AbstractRegistrationService {

    private final IDAOWorkshop idaoWorkshop;
    private final IDAOWorkshopRegistration idaoWorkshopRegistration;
    private final IDAOMember idaoMember;
    private final EmailService emailService;

    public WorkshopRegistrationService(
            IDAOWorkshop idaoWorkshop,
            IDAOWorkshopRegistration idaoWorkshopRegistration,
            IDAOMember idaoMember,
            EmailService emailService
    ) {
        this.idaoWorkshop = idaoWorkshop;
        this.idaoWorkshopRegistration = idaoWorkshopRegistration;
        this.idaoMember = idaoMember;
        this.emailService = emailService;
    }

    /**
     * Create a workshop registration for a visitor or a member.
     *
     * @param workshopId workshop identifier
     * @param workshopRegistrationDTO registration data
     * @param memberEmail connected member email, null for visitors
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> registerToWorkshop(
            Long workshopId,
            WorkshopRegistrationDTO workshopRegistrationDTO,
            String memberEmail
    ) {
        LogicResult<Void> validationResult = validateActivityId(workshopId);

        if (validationResult != null) {
            return validationResult;
        }

        Workshop workshop = idaoWorkshop.findById(workshopId);

        if (workshop == null) {
            return new LogicResult<>(
                    "404",
                    "Workshop not found",
                    null
            );
        }

        boolean isVisitor = memberEmail == null || memberEmail.isBlank();

        if (isVisitor && workshop.getVisibility() == Visibility.MEMBER_ONLY) {
            return new LogicResult<>(
                    "403",
                    "This workshop is reserved for members only",
                    null
            );
        }

        validationResult = validateRegistrationData(workshopRegistrationDTO);

        if (validationResult != null) {
            return validationResult;
        }

        String firstName = workshopRegistrationDTO.getFirstName();
        String lastName = workshopRegistrationDTO.getLastName();
        String email = workshopRegistrationDTO.getEmail();

        Member member = null;

        if (!isVisitor) {
            member = idaoMember.findByEmail(memberEmail);

            if (member == null) {
                return new LogicResult<>(
                        "404",
                        "Member not found",
                        null
                );
            }

            if (idaoWorkshopRegistration.existsByWorkshopAndMember(
                    workshop,
                    member
            )) {
                return new LogicResult<>(
                        "400",
                        "This member is already registered for this workshop",
                        null
                );
            }
        } else {
            if (idaoWorkshopRegistration.existsByWorkshopAndEmail(
                    workshop,
                    email
            )) {
                return new LogicResult<>(
                        "400",
                        "This email is already registered for this workshop",
                        null
                );
            }
        }

        WorkshopRegistration workshopRegistration =
                new WorkshopRegistration();

        workshopRegistration.setFirstName(firstName);
        workshopRegistration.setLastName(lastName);
        workshopRegistration.setEmail(email);
        workshopRegistration.setRegistrationDate(LocalDateTime.now());
        workshopRegistration.setStatus(RegistrationStatus.PENDING);
        workshopRegistration.setWorkshop(workshop);
        workshopRegistration.setMember(member);

        WorkshopRegistration savedRegistration =
                idaoWorkshopRegistration.create(workshopRegistration);

        if (savedRegistration == null) {
            return new LogicResult<>(
                    "500",
                    "Failed to create workshop registration",
                    null
            );
        }

        return new LogicResult<>(
                "201",
                "Workshop registration created successfully",
                null
        );
    }

    /**
     * Convert a workshop registration entity to an admin DTO.
     *
     * @param registration workshop registration entity
     * @return WorkshopRegistrationAdminDTO
     */
    private WorkshopRegistrationAdminDTO mapToWorkshopRegistrationAdminDTO(
            WorkshopRegistration registration
    ) {
        Long memberId = null;
        String memberEmail = null;

        if (registration.getMember() != null) {
            memberId = registration.getMember().getUserId();
            memberEmail = registration.getMember().getEmail();
        }

        return new WorkshopRegistrationAdminDTO(
                registration.getRegistrationId(),
                registration.getFirstName(),
                registration.getLastName(),
                registration.getEmail(),
                registration.getRegistrationDate(),
                registration.getStatus(),
                registration.getWorkshop().getWorkshopId(),
                registration.getWorkshop().getTitle(),
                memberId,
                memberEmail
        );
    }

    /**
     * Retrieve all registrations linked to a specific workshop.
     *
     * @param workshopId workshop identifier
     * @return LogicResult containing workshop registrations for admin display
     */
    public LogicResult<List<WorkshopRegistrationAdminDTO>> getRegistrationsByWorkshop(
            Long workshopId
    ) {
        LogicResult<Void> validationResult = validateActivityId(workshopId);
        if (validationResult != null) {
            return new LogicResult<>(
                    validationResult.getCode(),
                    validationResult.getMessage(),
                    null
            );
        }

        Workshop workshop = idaoWorkshop.findById(workshopId);

        if (workshop == null) {
            return new LogicResult<>("404", "Workshop not found", null);
        }

        List<WorkshopRegistration> registrations =
                idaoWorkshopRegistration.findByWorkshop(workshop);

        List<WorkshopRegistrationAdminDTO> registrationDTOs =
                registrations.stream()
                        .map(this::mapToWorkshopRegistrationAdminDTO)
                        .toList();

        return new LogicResult<>(
                "200",
                "Workshop registrations retrieved successfully",
                registrationDTOs
        );
    }

    /**
     * Approve a workshop registration.
     *
     * @param registrationId registration identifier
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> approveRegistration(Long registrationId) {
        LogicResult<Void> validationResult = validateRegistrationId(registrationId);
        if (validationResult != null) {
            return validationResult;
        }

        WorkshopRegistration registration =
                idaoWorkshopRegistration.findById(registrationId);

        if (registration == null) {
            return new LogicResult<>("404", "Workshop registration not found", null);
        }

        registration.setStatus(RegistrationStatus.APPROVED);

        WorkshopRegistration updatedRegistration =
                idaoWorkshopRegistration.update(registration);

        if (updatedRegistration == null) {
            return new LogicResult<>("500", "Failed to approve workshop registration", null);
        }

        emailService.sendWorkshopRegistrationAcceptedEmail(
                registration.getEmail(),
                registration.getFirstName(),
                registration.getWorkshop().getTitle()
        );

        return new LogicResult<>(
                "200",
                "Workshop registration approved successfully",
                null
        );
    }

    /**
     * Reject a workshop registration.
     *
     * @param registrationId registration identifier
     * @param refusalReason refusal reason
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> rejectRegistration(
            Long registrationId,
            String refusalReason
    ) {
        LogicResult<Void> validationResult = validateRegistrationId(registrationId);
        if (validationResult != null) {
            return validationResult;
        }

        validationResult = validateRefusalReason(refusalReason);
        if (validationResult != null) {
            return validationResult;
        }

        WorkshopRegistration registration =
                idaoWorkshopRegistration.findById(registrationId);

        if (registration == null) {
            return new LogicResult<>("404", "Workshop registration not found", null);
        }

        registration.setStatus(RegistrationStatus.REJECTED);

        WorkshopRegistration updatedRegistration =
                idaoWorkshopRegistration.update(registration);

        if (updatedRegistration == null) {
            return new LogicResult<>("500", "Failed to reject workshop registration", null);
        }

        emailService.sendWorkshopRegistrationRejectedEmail(
                registration.getEmail(),
                registration.getFirstName(),
                registration.getWorkshop().getTitle(),
                refusalReason
        );

        return new LogicResult<>(
                "200",
                "Workshop registration rejected successfully",
                null
        );
    }

    /**
     * Cancel a workshop registration.
     *
     * @param registrationId registration identifier
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> cancelRegistration(Long registrationId) {
        LogicResult<Void> validationResult = validateRegistrationId(registrationId);
        if (validationResult != null) {
            return validationResult;
        }

        WorkshopRegistration registration =
                idaoWorkshopRegistration.findById(registrationId);

        if (registration == null) {
            return new LogicResult<>("404", "Workshop registration not found", null);
        }

        registration.setStatus(RegistrationStatus.CANCELLED);

        WorkshopRegistration updatedRegistration =
                idaoWorkshopRegistration.update(registration);

        if (updatedRegistration == null) {
            return new LogicResult<>("500", "Failed to cancel workshop registration", null);
        }

        return new LogicResult<>(
                "200",
                "Workshop registration cancelled successfully",
                null
        );
    }


}