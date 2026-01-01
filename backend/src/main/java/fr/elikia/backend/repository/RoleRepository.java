package fr.elikia.backend.repository;

import fr.elikia.backend.bo.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
