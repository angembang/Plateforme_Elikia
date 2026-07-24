package fr.elikia.backend.repository;

import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.Workshop;
import fr.elikia.backend.bo.WorkshopRegistration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkshopRegistrationRepository extends JpaRepository<WorkshopRegistration, Long> {
    /**
     * Check whether a visitor is already registered
     * for the same workshop using the email address.
     *
     * @param workshop workshop
     * @param email visitor email
     * @return true if a registration already exists
     */
    boolean existsByWorkshopAndEmail(
            Workshop workshop,
            String email
    );

    /**
     * Check whether a member is already registered
     * for the same workshop.
     *
     * @param workshop workshop
     * @param member member
     * @return true if a registration already exists
     */
    boolean existsByWorkshopAndMember(
            Workshop workshop,
            Member member
    );

    /**
     * Retrieve all registrations linked to a workshop.
     *
     * @param workshop workshop
     * @return workshop registrations
     */
    List<WorkshopRegistration> findByWorkshop(
            Workshop workshop
    );
}
