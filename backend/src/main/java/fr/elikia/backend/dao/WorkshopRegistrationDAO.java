package fr.elikia.backend.dao;

import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.Workshop;
import fr.elikia.backend.bo.WorkshopRegistration;
import fr.elikia.backend.dao.idao.IDAOWorkshopRegistration;
import fr.elikia.backend.repository.WorkshopRegistrationRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO responsible for managing workshop registrations.
 *
 * This class delegates database operations
 * to the WorkshopRegistrationRepository.
 */
@Repository
public class WorkshopRegistrationDAO implements IDAOWorkshopRegistration {

    private final WorkshopRegistrationRepository workshopRegistrationRepository;

    public WorkshopRegistrationDAO(
            WorkshopRegistrationRepository workshopRegistrationRepository
    ) {
        this.workshopRegistrationRepository = workshopRegistrationRepository;
    }

    /**
     * Create a new workshop registration.
     *
     * @param workshopRegistration workshop registration to create
     * @return created workshop registration
     */
    @Override
    public WorkshopRegistration create(WorkshopRegistration workshopRegistration) {
        return workshopRegistrationRepository.save(workshopRegistration);
    }

    /**
     * Update an existing workshop registration.
     *
     * @param workshopRegistration workshop registration to update
     * @return updated workshop registration
     */
    @Override
    public WorkshopRegistration update(WorkshopRegistration workshopRegistration) {
        return workshopRegistrationRepository.save(workshopRegistration);
    }

    /**
     * Retrieve a workshop registration by its identifier.
     *
     * @param registrationId registration identifier
     * @return workshop registration or null if not found
     */
    @Override
    public WorkshopRegistration findById(Long registrationId) {
        return workshopRegistrationRepository
                .findById(registrationId)
                .orElse(null);
    }

    /**
     * Retrieve all registrations linked to a workshop.
     *
     * @param workshop workshop
     * @return workshop registrations
     */
    @Override
    public List<WorkshopRegistration> findByWorkshop(Workshop workshop) {
        return workshopRegistrationRepository.findByWorkshop(workshop);
    }

    /**
     * Check whether a visitor is already registered
     * for the specified workshop.
     *
     * @param workshop workshop
     * @param email visitor email
     * @return true if the registration already exists
     */
    @Override
    public boolean existsByWorkshopAndEmail(
            Workshop workshop,
            String email
    ) {
        return workshopRegistrationRepository
                .existsByWorkshopAndEmail(workshop, email);
    }

    /**
     * Check whether a member is already registered
     * for the specified workshop.
     *
     * @param workshop workshop
     * @param member member
     * @return true if the registration already exists
     */
    @Override
    public boolean existsByWorkshopAndMember(
            Workshop workshop,
            Member member
    ) {
        return workshopRegistrationRepository
                .existsByWorkshopAndMember(workshop, member);
    }
}