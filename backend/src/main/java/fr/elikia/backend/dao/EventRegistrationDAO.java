package fr.elikia.backend.dao;

import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.EventRegistration;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.dao.idao.IDAOEventRegistration;
import fr.elikia.backend.repository.EventRegistrationRepository;
import org.springframework.stereotype.Component;

/**
 * Implémentation de l'interface IDAOEventRegistration.
 * Cette classe assure la communication entre la couche métier
 * et le repository en centralisant les opérations liées
 * aux inscriptions aux événements.
 */
@Component
public class EventRegistrationDAO implements IDAOEventRegistration {
    private final EventRegistrationRepository eventRegistrationRepository;

    public EventRegistrationDAO(EventRegistrationRepository eventRegistrationRepository) {
        this.eventRegistrationRepository = eventRegistrationRepository;
    }
    /**
     * Créer une nouvelle inscription à un événement
     *
     * @param eventRegistration l'inscription à créer
     * @return EventRegistration
     */
    @Override
    public EventRegistration create(EventRegistration eventRegistration) {
        return eventRegistrationRepository.save(eventRegistration);
    }

    /**
     * Vérifier si une adresse email est déjà inscrite à un événement
     *
     * @param event l'événement concerné
     * @param email l'adresse email à vérifier
     * @return boolean
     */
    @Override
    public boolean existsByEventAndEmail(Event event, String email) {
        return eventRegistrationRepository.existsByEventAndEmail(event, email);
    }

    /**
     * Vérifier si un membre est déjà inscrit à un événement
     *
     * @param event l'événement concerné
     * @param member le membre à vérifier
     * @return boolean
     */
    @Override
    public boolean existsByEventAndMember(Event event, Member member) {
        return eventRegistrationRepository.existsByEventAndMember(event, member);
    }
}
