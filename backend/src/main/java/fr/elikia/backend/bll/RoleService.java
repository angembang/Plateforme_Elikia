package fr.elikia.backend.bll;

import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Role;
import fr.elikia.backend.dao.idao.IDAORole;
import fr.elikia.backend.dto.RoleDTO;
import fr.elikia.backend.security.InputSanitizer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for managing member roles
 * It handles role creation , validation, sanitization
 * and business rules relates to roles.
 */
@Service
public class RoleService {
    private final IDAORole idaoRole;

    public RoleService(IDAORole idaoRole) {
        this.idaoRole = idaoRole;
    }

    /**
     * Creates a new role.
     * Only administrators are allowed to create roles.
     *
     * @param roleDTO Role data received from the client
     * @return LogicResult containing the created role or an error
     */
    public LogicResult<Role> createRole(RoleDTO roleDTO) {

        // Default error response
        LogicResult<Role> result =
                new LogicResult<>("400", "Validation error", null);

        // ======================================
        // Sanitize input (XSS protection)
        // ======================================
        String roleName = roleDTO.getName() != null
                ? InputSanitizer.sanitize(roleDTO.getName().toUpperCase())
                : null;

        // ======================================
        // Validation
        // ======================================
        if (!isValidRoleName(roleName, result)) {
            return result;
        }

        // ======================================
        // Check uniqueness
        // ======================================
        if (idaoRole.findByName(roleName) != null) {
            result.setCode("409");
            result.setMessage("Role already exists");
            return result;
        }

        // ======================================
        // Create role
        // ======================================
        Role role = new Role();
        role.setName(roleName);

        Role savedRole = idaoRole.create(role);

        if (savedRole == null) {
            result.setCode("500");
            result.setMessage("Failed to create role");
            return result;
        }

        return new LogicResult<>(
                "201",
                "Role created successfully",
                savedRole
        );
    }

    // =========================================================
    // PRIVATE VALIDATION METHODS
    // =========================================================

    /**
     * Validates role name.
     */
    private boolean isValidRoleName(String roleName, LogicResult<?> result) {
        if (roleName == null || roleName.isBlank()) {
            result.setMessage("Role name is required");
            return false;
        }

        if (roleName.length() < 2 || roleName.length() > 30) {
            result.setMessage("Role name must be between 2 and 30 characters");
            return false;
        }

        return true;
    }


    /**
     * Retrieve all roles
     */
    public LogicResult<List<Role>> getAllRoles() {
        return new LogicResult<>("200", "Roles retrieved", idaoRole.findAll());
    }


    /**
     * Retrieve role by ID
     */
    public LogicResult<Role> getRoleById(Long roleId) {
        Role role = idaoRole.findById(roleId);
        if (role == null) {
            return new LogicResult<>("404", "Role not found", null);
        }
        return new LogicResult<>("200", "Role found", role);
    }


    /**
     * Delete role
     */
    public LogicResult<Void> deleteRole(Long roleId) {
        if (!idaoRole.deleteById(roleId)) {
            return new LogicResult<>("404", "Role not found", null);
        }
        return new LogicResult<>("200", "Role deleted", null);
    }
}
