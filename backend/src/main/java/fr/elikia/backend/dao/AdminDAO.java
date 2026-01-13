package fr.elikia.backend.dao;

import fr.elikia.backend.bo.Admin;

import fr.elikia.backend.dao.idao.IDAOAdmin;
import fr.elikia.backend.repository.AdminRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdminDAO implements IDAOAdmin {
    private final AdminRepository adminRepository;

    public AdminDAO(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public Admin findById(Long adminId){
        return adminRepository.findById(adminId).orElse(null);
    }

    public Admin findByEmail(String adminEmail) {
        return adminRepository.findByEmail(adminEmail).orElse(null);

    }

    @Override
    public boolean deleteById(Long adminId){
        Admin admin = adminRepository.findById(adminId).orElse(null);
        if(admin!=null){
            adminRepository.delete(admin);
            return true;
        }
        return false;
    }

    @Override
    public Admin create(Admin admin){
        // check if the member already exists
        if(adminRepository.findByEmail(admin.getEmail()).isPresent()){
            return null;
        }
        // Save the admin
        return adminRepository.save(admin);
    }

    public Admin update(Admin admin){
        // Check if the admin exists
        if(!adminRepository.existsById(admin.getUserId())) {
            return null;
        }
        return adminRepository.save(admin);
    }

}
