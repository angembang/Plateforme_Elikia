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

    public MemberService(IDAOMember idaoMember, IDAORole idaoRole) {
        this.idaoMember = idaoMember;
        this.idaoRole = idaoRole;
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
     * @return le membre mis à jour
     */
    public LogicResult<Member> acceptMembership(Long id) {
        Member member = idaoMember.findById(id);

        if (member == null) {
            return new LogicResult<>("404", "Membre introuvable", null);
        }

        member.setStatus("VALIDE");
        member.setMembershipDate(LocalDate.now());

        if (member.getMembershipNumber() == null || member.getMembershipNumber().isBlank()) {
            member.setMembershipNumber(generateMembershipNumber(member));
        }

        return new LogicResult<>("200", "Demande d'adhésion acceptée", idaoMember.updateByAdmin(member));
    }

    /**
     * Refuse une demande d'adhésion.
     * Le motif est reçu depuis l'interface admin mais n'est pas encore sauvegardé,
     * car l'entité Member ne contient pas de champ dédié au motif de refus.
     *
     * @param id identifiant du membre
     * @param reason motif du refus
     * @return le membre mis à jour
     */
    public LogicResult<Member> rejectMembership(Long id, String reason) {
        Member member = idaoMember.findById(id);

        if (member == null) {
            return new LogicResult<>("404", "Membre introuvable", null);
        }

        member.setStatus("REFUSEE");

        return new LogicResult<>("200", "Demande d'adhésion refusée", idaoMember.updateByAdmin(member));
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
