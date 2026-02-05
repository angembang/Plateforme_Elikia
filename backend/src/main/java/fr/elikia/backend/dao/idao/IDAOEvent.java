package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Event;

import java.util.List;

public interface IDAOEvent {
    List<Event> findAll();

    Event findById(Long eventId);

    boolean deleteById(Long eventId);

    Event create(Event event);

    Event update(Event event);
}
