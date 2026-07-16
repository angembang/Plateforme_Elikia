package fr.elikia.backend.bll;

import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.Role;
import fr.elikia.backend.bo.enums.RegistrationStatus;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.dao.idao.IDAORole;
import fr.elikia.backend.dto.AdminUpdateMemberDTO;
import fr.elikia.backend.dto.MemberAdminDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for managing members
 * It handles members retrieval, the updated
 * and deleted
 */
@Service
public class MemberService {
    private final IDAOMember idaoMember;
    private final IDAORole idaoRole;
    private final EmailService emailService;
    private static final String MEMBERSHIP_PREFIX = "ELK";

    public MemberService(IDAOMember idaoMember, IDAORole idaoRole , EmailService emailService) {
        this.idaoMember = idaoMember;
        this.idaoRole = idaoRole;
        this.emailService = emailService;
    }

    /**
     * Retrieve all members
     */
    public LogicResult<List<MemberAdminDTO>> findAll() {
        List<MemberAdminDTO> members = idaoMember.findAll()
                .stream()
                .map(MemberAdminDTO::new)
                .toList();

        return new LogicResult<>("200", "Members retrieved", members);
    }

    /**
     * Update member status and role
     */
    public LogicResult<Member> updateMember(Long id, AdminUpdateMemberDTO dto) {

        Member existingMember = findMember(id);

        if (existingMember == null) {
            return new LogicResult<>("404", "Member not found", null);
        }

        if (dto == null) {
            return new LogicResult<>("400", "Member update data is required", null);
        }
        RegistrationStatus status = dto.getStatus();

        if (status != null) {
            existingMember.setStatus(status);
        }

        String roleName = dto.getRoleName();

        if (roleName != null && !roleName.isBlank()) {
            Role role = idaoRole.findByName(roleName);

            if (role == null) {
                return new LogicResult<>("404", "Role not found", null);
            }

            existingMember.setRole(role);
        }

        Member updatedMember = idaoMember.updateByAdmin(existingMember);

        if (updatedMember == null) {
            return new LogicResult<>("500", "Failed to update member", null);
        }

        return new LogicResult<>("200", "Member updated", updatedMember);
    }


    /**
     * Delete member
     */
    public LogicResult<Void> delete(Long id) {
        if (!idaoMember.deleteById(id)) {
            return new LogicResult<>("404", "Member not found", null);
        }
        return new LogicResult<>("200", "Member deleted", null);
    }

    /**
     * Retrieve all membership request with PENDING status
     *
     * @return the list of pending membership requests
     */
    public LogicResult<List<Member>> findPendingMembershipRequests() {
        List<Member> pendingMembers = idaoMember.findAll()
                .stream()
                .filter(member -> RegistrationStatus.PENDING.equals(member.getStatus()))
                .toList();

        return new LogicResult<>("200", "Demandes d'adhésion récupérées avec succès", pendingMembers);
    }

    /**
     * Approve a membership registration
     * The member status is set to APPROVED, a membership number is generated,
     * and the membership registration date is set
     *
     * @param id member identifier
     * @return updated membership information
     */
    public LogicResult<MemberAdminDTO> acceptMembership(Long id) {
        Member member = findMember(id);

        if (member == null) {
            return new LogicResult<>("404", "Member not found", null);
        }

        if(!isPending(member)) {
            return new LogicResult<>("409", "Only members with pendingstatus can be approved", null);
        }

        member.setStatus(RegistrationStatus.APPROVED);
        member.setMembershipDate(LocalDate.now());

        if (member.getMembershipNumber() == null || member.getMembershipNumber().isBlank()) {
            member.setMembershipNumber(generateMembershipNumber(member));
        }

        Member updatedMember = idaoMember.updateByAdmin(member);

        if (updatedMember == null) {
            return new LogicResult<>("500", "Failed to approve membership request", null);
        }

        emailService.sendMembershipAcceptedEmail(
                updatedMember.getEmail(),
                updatedMember.getFirstName(),
                updatedMember.getMembershipNumber()
        );

        return new LogicResult<>("200", "Membership request approved", new MemberAdminDTO(updatedMember));
    }

    /**
     * Reject a membership registration.
     * The reason is provided by the admin and used in the rejection email.
     * The reason is not stored in the database.
     *
     * @param id member identifier
     * @param reason reason rejection
     * @return updated membership information
     */
    public LogicResult<MemberAdminDTO> rejectMembership(Long id, String reason) {
        if (id == null || id <= 0) {
            return new LogicResult<>("400", "Invalid member identifier", null);
        }

        Member member = idaoMember.findById(id);

        if (member == null) {
            return new LogicResult<>("404", "Member not found", null);
        }

        if(!isPending(member)) {
            return new LogicResult<>("409", "ONLY members with pending status can be Rejected", null);
        }


        if (reason == null || reason.isBlank()) {
            return new LogicResult<>("400", "Rejection reason is required", null);
        }

        member.setStatus(RegistrationStatus.REJECTED);

        Member updatedMember = idaoMember.updateByAdmin(member);

        if (updatedMember == null) {
            return new LogicResult<>("500", "Failed to reject membership request", null);
        }

        emailService.sendMembershipRejectedEmail(
                updatedMember.getEmail(),
                updatedMember.getFirstName(),
                reason
        );

        return new LogicResult<>(
                "200",
                "Membership request rejected",
                new MemberAdminDTO(updatedMember)
        );
    }

    /**
     * Generate a membership number
     *
     * @param member validate member
     * @return generate membership number
     */
    private String generateMembershipNumber(Member member) {
        return MEMBERSHIP_PREFIX + "-" + LocalDate.now().getYear() + "-" + String.format("%05d", member.getUserId());
    }

    private boolean isPending(Member member) {
        return member.getStatus() == RegistrationStatus.PENDING;
    }


    private Member findMember(Long id) {
        if(id == null || id <= 0) {
            return null;
        }
        return idaoMember.findById(id);
    }
}
