package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Admin;
import fr.elikia.backend.bo.Member;

import java.util.List;

public interface IDAOAdmin {
    public List<Admin> findAll();

    public Admin findById(Long adminId);

    public Admin findByEmail(String adminEmail);

    public boolean deleteById(Long adminId);

    public Admin create(Admin admin);

    public Admin update(Admin admin);
}
