package fr.elikia.backend.controller;

import fr.elikia.backend.bll.RoleService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Role;
import fr.elikia.backend.dto.RoleDTO;
import fr.elikia.backend.security.jwt.RequiredJWTAuth;
import fr.elikia.backend.security.jwt.RequiredRole;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for managing roles.
 * Only ADMIN users are allowed to create, update or delete roles
 */
@RestController
@RequestMapping("/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    /**
     * Create a new role
     *
     * @param dto role data
     * @return created role
     */
    @PostMapping("/add")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public LogicResult<Role> createRole(@RequestBody RoleDTO dto) {
        return roleService.createRole(dto);
    }


    /**
     * Retrieve all roles
     *
     * @return list of roles
     */

    @GetMapping("")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public LogicResult<List<Role>> getAllRoles() {
        return roleService.getAllRoles();
    }

    /**
     * Retrieve a role by its ID
     *
     * @param roleId role identifier
     * @return role if found
     */
    @GetMapping("/{id}")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public LogicResult<Role> getRoleById(@PathVariable("id") Long roleId) {
        return roleService.getRoleById(roleId);
    }

    /**
     * Delete a role by its ID
     *
     * @param roleId role identifier
     * @return deletion result
     */
    @DeleteMapping("delete/{id}")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public LogicResult<Void> deleteRole(@PathVariable("id") Long roleId) {
        return roleService.deleteRole(roleId);
    }
}
