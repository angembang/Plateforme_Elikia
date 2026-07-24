package fr.elikia.backend.dao;

import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.EventRegistration;
import fr.elikia.backend.bo.Member;
import fr.elikia.backend.dao.idao.IDAOEventRegistration;
import fr.elikia.backend.repository.EventRegistrationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

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
     * Rechercher une inscription à un événement par son identifiant.
     *
     * @param registrationId l'identifiant de l'inscription
     * @return l'inscription trouvée ou null si aucune inscription ne correspond
     */
    @Override
    public EventRegistration findById(Long registrationId) {
        return eventRegistrationRepository.findById(registrationId).orElse(null);
    }

    /**
     * Récupérer toutes les inscriptions associées à un événement.
     *
     * @param event l'événement concerné
     * @return la liste des inscriptions de cet événement
     */
    @Override
    public List<EventRegistration> findByEvent(Event event) {
        return eventRegistrationRepository.findByEvent(event);
    }

    /**
     * Mettre à jour une inscription existante.
     * La mise à jour est effectuée uniquement si l'inscription existe déjà.
     *
     * @param eventRegistration l'inscription à mettre à jour
     * @return l'inscription mise à jour ou null si elle n'existe pas
     */
    @Override
    public EventRegistration update(EventRegistration eventRegistration) {
        if (!eventRegistrationRepository.existsById(eventRegistration.getRegistrationId())) {
            return null;
        }
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
