package fr.elikia.backend.repository;

import fr.elikia.backend.bo.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
}
