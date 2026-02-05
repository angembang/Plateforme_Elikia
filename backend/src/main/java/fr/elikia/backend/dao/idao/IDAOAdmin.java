package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Admin;

import java.util.List;

public interface IDAOAdmin {
    List<Admin> findAll();

    Admin findById(Long adminId);

    Admin findByEmail(String adminEmail);

    boolean deleteById(Long adminId);

    Admin create(Admin admin);

    Admin update(Admin admin);
}
