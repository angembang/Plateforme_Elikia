package fr.elikia.backend.dao;

import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.Visibility;
import fr.elikia.backend.dao.idao.IDAOEvent;
import fr.elikia.backend.repository.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventDAO implements IDAOEvent {
    private final EventRepository eventRepository;

    public EventDAO(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Retrieve the all events
     */
    @Override
    public List<Event> findAll() {
        return eventRepository.findAll();
    }


    /**
     * Retrieve the event by its visibility ordered by the start date desc
     */
    @Override
    public Page<Event> findAllByVisibilityOrderByStartDateDesc(
            Visibility visibility,
            Pageable pageable
    ) {
        return eventRepository.findAllByVisibilityOrderByStartDateDesc(
                visibility,
                pageable
        );

    }


    /**
     * Retrieve the all events ordered by the start date
     */
    @Override
    public Page<Event> findAllByOrderByStartDateDesc(
            Pageable pageable
    ) {
        return eventRepository.findAllByOrderByStartDateDesc(
                pageable
        );

    }


    /**
     * Retrieve the 4 latest events
     */
    @Override
    public List<Event> findAllByOrderByStartDateDesc() {
        return eventRepository.findAllByOrderByStartDateDesc();
    }


    /**
     * Retrieve the event by its unique identifier
     * @param eventId the unique identifier of the event
     *
     * @return Event | null the retrieved event or null if no event retrieved
     */
    @Override
    public Event findById(Long eventId) {
        return eventRepository.findById(eventId).orElse(null);
    }


    /***
     * Delete the event by its unique identifier
     * @param eventId the unique identifier of the event
     * @return boolean
     */
    @Override
    public boolean deleteById(Long eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);

        if(event != null) {
            eventRepository.delete(event);
            return true;
        }
        return false;
    }

    /**
     * Create a new event
     * @param event the event to created
     * @return Event
     */
    @Override
    public Event create(Event event) {
        return eventRepository.save(event);
    }

    /**
     * Update an event
     * @param event the event to update
     * @return Event the updated event
     */
    @Override
    public Event update(Event event) {
        if(!eventRepository.existsById(event.getEventId())) {
            return null;
        }
        return eventRepository.save(event);
    }
}
