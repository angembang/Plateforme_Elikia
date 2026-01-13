package fr.elikia.backend.dao;

import fr.elikia.backend.bo.Role;
import fr.elikia.backend.dao.idao.IDAORole;
import fr.elikia.backend.repository.RoleRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleDAO implements IDAORole {
    private final RoleRepository roleRepository;

    public RoleDAO(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    /**
     * Method to retrieve all roles
     *
     * @return List<Role>
     */
    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    /**
     * Method to retrieve a role by its unique identifier
     *
     * @return role | null
     */
    @Override
    public Role findById(Long roleId) {
        return roleRepository.findById(roleId).orElse(null);
    }

    /**
     * Method to retrieve a role by its name
     *
     * @return role | null
     */
    @Override
    public Role findByName(String roleName) {
        return roleRepository.findByName(roleName).orElse(null);
    }

    /**
     * Method to delete a role by its unique identifier
     *
     * @return boolean
     */
    @Override
    public boolean deleteById(Long roleId) {
        Role role = roleRepository.findById(roleId).orElse(null);
        if(role != null) {
            roleRepository.delete(role);
            return true;
        }
        return false;

    }

    /**
     * Method to create a role
     *
     * @return role | null
     */
    @Override
    public Role create(Role role) {
        // check if the role already exists
        if(roleRepository.findByName(role.getName()).isPresent()){
            return null;
        }
        // Save the role
        return roleRepository.save(role);

    }

    /**
     * Method to update a role
     *
     * @return role | null
     */
    @Override
    public Role update(Role role) {
        // Check if the role exists
        if(!roleRepository.existsById(role.getRoleId())) {
            return null;
        }
        return roleRepository.save(role);

    }

}
