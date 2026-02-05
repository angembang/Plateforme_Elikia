package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Role;

import java.util.List;

public interface IDAORole {
    List<Role> findAll();

    Role findById(Long roleId);

    Role findByName(String roleName);

    boolean deleteById(Long roleId);

    Role create(Role role);

    Role update(Role role);
}
