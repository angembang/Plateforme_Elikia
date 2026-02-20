package fr.elikia.backend.dao.idao;

import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.enums.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IDAOEvent {
    List<Event> findAll();

    // Retrieve events by their visibility ordered by the start date desc
    Page<Event> findAllByVisibilityOrderByStartDateDesc(
            Visibility visibility,
            Pageable pageable
    );


    // Retrieve all events ordered by the start date
    Page<Event> findAllByOrderByStartDateDesc(
            Pageable pageable
    );

    // Retrieve 4 latest events
    List<Event> findAllByOrderByStartDateDesc();

    Event findById(Long eventId);

    boolean deleteById(Long eventId);

    Event create(Event event);

    Event update(Event event);
}
