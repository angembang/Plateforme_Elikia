package fr.elikia.backend.dao;

import fr.elikia.backend.bo.Event;
import fr.elikia.backend.dao.idao.IDAOEvent;
import fr.elikia.backend.repository.EventRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventDAO implements IDAOEvent {
    private final EventRepository eventRepository;

    public EventDAO(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }

    @Override
    public Event findById(Long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }

    @Override
    public boolean deleteById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);

        if(event != null) {
            eventRepository.delete(event);
            return true;
        }
        return false;
    }

    @Override
    public Event create(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event update(Event event) {
        if(!eventRepository.existsById(event.getEventId())) {
            return null;
        }
        return eventRepository.save(event);
    }
}
