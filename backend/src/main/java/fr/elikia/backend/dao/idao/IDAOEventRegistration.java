package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.EventRegistration;
import fr.elikia.backend.bo.Member;

import java.util.List;

/**
 * Interface définissant les opérations d'accès aux données
 * liées aux inscriptions aux événements.
 * Elle est utilisée par la couche métier afin de manipuler
 * les inscriptions sans dépendre directement du repository.
 */
public interface IDAOEventRegistration {
    EventRegistration create(EventRegistration eventRegistration);

    EventRegistration findById(Long registrationId);

    List<EventRegistration> findByEvent(Event event);

    EventRegistration update(EventRegistration eventRegistration);

    boolean existsByEventAndEmail(Event event, String email);

    boolean existsByEventAndMember(Event event, Member member);


}
