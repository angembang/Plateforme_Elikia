package fr.elikia.backend.repository;

import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.EventRegistration;
import fr.elikia.backend.bo.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository responsable de l'accès aux données des inscriptions aux événements.
 * Il fournit les opérations CRUD de base grâce à JpaRepository
 * ainsi que des méthodes spécifiques pour vérifier les inscriptions existantes.
 */
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    boolean existsByEventAndEmail(Event event, String email);

    boolean existsByEventAndMember(Event event, Member member);

    List<EventRegistration> findByEvent(Event event);
}
