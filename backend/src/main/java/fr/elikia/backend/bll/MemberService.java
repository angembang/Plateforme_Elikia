package fr.elikia.backend.bll;

import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.Role;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.dao.idao.IDAORole;
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

    public MemberService(IDAOMember idaoMember, IDAORole idaoRole, EmailService emailService) {
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
    public LogicResult<Member> updateMember(Long id, String status, String roleName) {
        Member member = idaoMember.findById(id);
        if (member == null) {
            return new LogicResult<>("404", "Member not found", null);
        }

        if (status != null) {
            member.setStatus(status);
        }

        if (roleName != null) {
            Role role = idaoRole.findByName(roleName);
            if (role == null) {
                return new LogicResult<>("404", "Role not found",null);
            }
            member.setRole(role);
        }

        return new LogicResult<>("200", "Member updated", idaoMember.updateByAdmin(member));
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
     * Récupère les demandes d'adhésion en attente de validation.
     *
     * @return la liste des membres dont le statut est INSCRIPTION_TRANSMISE
     */
    public LogicResult<List<Member>> findPendingMembershipRequests() {
        List<Member> pendingMembers = idaoMember.findAll()
                .stream()
                .filter(member -> "INSCRIPTION_TRANSMISE".equals(member.getStatus()))
                .toList();

        return new LogicResult<>("200", "Demandes d'adhésion récupérées avec succès", pendingMembers);
    }

    /**
     * Accepte une demande d'adhésion.
     * Le statut du membre devient VALIDE, un numéro d'adhésion est généré
     * et la date d'adhésion est renseignée.
     *
     * @param id identifiant du membre
     * @return les informations du membre mises à jour
     */
    public LogicResult<MemberAdminDTO> acceptMembership(Long id) {
        Member member = idaoMember.findById(id);

        if (member == null) {
            return new LogicResult<>("404", "Membre introuvable", null);
        }

        member.setStatus("VALIDE");
        member.setMembershipDate(LocalDate.now());

        if (member.getMembershipNumber() == null || member.getMembershipNumber().isBlank()) {
            member.setMembershipNumber(generateMembershipNumber(member));
        }

        Member updatedMember = idaoMember.updateByAdmin(member);

        emailService.sendMembershipAcceptedEmail(
                updatedMember.getEmail(),
                updatedMember.getFirstName(),
                updatedMember.getMembershipNumber()
        );

        return new LogicResult<>("200", "Demande d'adhésion acceptée", new MemberAdminDTO(updatedMember));
    }

    /**
     * Refuse une demande d'adhésion.
     * Le motif est reçu depuis l'interface administrateur afin d'être utilisé
     * lors de l'envoi de l'email de refus.
     * Conformément au modèle de données actuel, ce motif n'est pas conservé
     * en base de données.
     *
     * @param id identifiant du membre
     * @param reason motif du refus
     * @return les informations du membre mises à jour
     */
    public LogicResult<MemberAdminDTO> rejectMembership(Long id, String reason) {
        Member member = idaoMember.findById(id);

        if (member == null) {
            return new LogicResult<>("404", "Membre introuvable", null);
        }

        member.setStatus("REFUSEE");

        Member updatedMember = idaoMember.updateByAdmin(member);

        emailService.sendMembershipRejectedEmail(
                updatedMember.getEmail(),
                updatedMember.getFirstName(),
                reason
        );

        return new LogicResult<>("200", "Demande d'adhésion refusée", new MemberAdminDTO(updatedMember));
    }

    /**
     * Génère un numéro d'adhésion unique basé sur l'année courante
     * et l'identifiant du membre.
     *
     * @param member membre validé
     * @return numéro d'adhésion généré
     */
    private String generateMembershipNumber(Member member) {
        return "ELK-" + LocalDate.now().getYear() + "-" + String.format("%05d", member.getUserId());
    }
}
