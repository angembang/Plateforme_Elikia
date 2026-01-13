package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.Role;

import java.util.List;

public interface IDAORole {
    public List<Role> findAll();

    public Role findById(Long roleId);

    public Role findByName(String roleName);

    public boolean deleteById(Long roleId);

    public Role create(Role role);

    public Role update(Role role);
}
