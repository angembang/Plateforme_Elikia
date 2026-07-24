package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Member;
import fr.elikia.backend.bo.Workshop;
import fr.elikia.backend.bo.WorkshopRegistration;

import java.util.List;

/**
 * DAO interface responsible for managing workshop registrations.
 *
 * It provides CRUD operations and custom queries
 * required by the workshop registration service.
 */
public interface IDAOWorkshopRegistration {

    /**
     * Create a new workshop registration.
     *
     * @param workshopRegistration workshop registration to create
     * @return created workshop registration
     */
    WorkshopRegistration create(WorkshopRegistration workshopRegistration);

    /**
     * Update an existing workshop registration.
     *
     * @param workshopRegistration workshop registration to update
     * @return updated workshop registration
     */
    WorkshopRegistration update(WorkshopRegistration workshopRegistration);

    /**
     * Retrieve a workshop registration by its identifier.
     *
     * @param registrationId registration identifier
     * @return workshop registration or null if not found
     */
    WorkshopRegistration findById(Long registrationId);

    /**
     * Retrieve all registrations linked to a workshop.
     *
     * @param workshop workshop
     * @return workshop registrations
     */
    List<WorkshopRegistration> findByWorkshop(Workshop workshop);

    /**
     * Check whether a visitor is already registered
     * for the specified workshop.
     *
     * @param workshop workshop
     * @param email visitor email
     * @return true if the registration already exists
     */
    boolean existsByWorkshopAndEmail(
            Workshop workshop,
            String email
    );

    /**
     * Check whether a member is already registered
     * for the specified workshop.
     *
     * @param workshop workshop
     * @param member member
     * @return true if the registration already exists
     */
    boolean existsByWorkshopAndMember(
            Workshop workshop,
            Member member
    );
}