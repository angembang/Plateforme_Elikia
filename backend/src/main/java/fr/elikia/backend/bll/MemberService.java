package fr.elikia.backend.bll;

import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.Role;
import fr.elikia.backend.dao.idao.IDAOMember;
import fr.elikia.backend.dao.idao.IDAORole;
import org.springframework.stereotype.Service;

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
    public LogicResult<List<Member>> findAll() {
        return new LogicResult<>("200", "Members retrieved", idaoMember.findAll());
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
                return new LogicResult<>("404", "Role not found", null);
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
}
