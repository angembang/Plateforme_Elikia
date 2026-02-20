package fr.elikia.backend.bll;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.elikia.backend.bo.*;
import fr.elikia.backend.bo.enums.ActivityOwnerType;
import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.dao.idao.IDAOEvent;
import fr.elikia.backend.dto.EventDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service responsible for managing Event domain logic.

 * This service handles:
 * - Strong input validation and sanitization (XSS protection)
 * - Business rules for the creation, updating and retrieved of the event
 *
 */
@Service
public class EventService extends AbstractActivityService {
    public static final String START_DATE = "startDate";
    public static final String NO_EVENT_FOUND = "No event found";
    public static final String EVENT_PAGE_RETRIEVED = "Event page retrieved";

    // Dependencies
    private final IDAOEvent idaoEvent;


    public EventService(IDAOEvent idaoEvent, MediaService mediaService, ObjectMapper objectMapper) {
        super(mediaService, objectMapper);
        this.idaoEvent = idaoEvent;
    }
    /**
     * Creates an Event entity and uploads/attaches multiple Media files to it.

     * Workflow:
     * - Validate and sanitize EventDTO
     * - Create and persist the Event entity
     * - For each uploaded file:
     * - Delegate Media creation to MediaService
     * - Attach Media to the Event
     * - Persist final Event with its medias

     * Business rules:
     * - Event must be created before any Media
     * - Each uploaded file creates exactly one Media
     * - Stop immediately if any Media creation fails
     *
     * @param eventDTO Data transfer object containing event fields
     * @param files Optional list of uploaded media files
     *
     * @return LogicResult indicating success or failure
     */
    @Transactional
    public LogicResult<Void> createEvent(
            EventDTO eventDTO,
            String videoUrl,
            List<MultipartFile> files) {

        // Prepare a default validation error result
        LogicResult<Void> result = validationError();

        // Validate and sanitize Event input
        SanitizedActivityInput input = validateAndSanitizeActivity(eventDTO, result);
        if (input == null) {
            // Validation failed, result already filled
            return result;
        }

        // Create Event entity
        Event event = new Event();
        applySanitizedValues(event, input);

        // Persist Event first to generate its identifier
        idaoEvent.create(event);
        Long generatedEventId = event.getEventId();
        if(generatedEventId == null) {
            throw new IllegalStateException("event identifier was not generated");
        }

        /* ********* create media ******** */
        // Handle all media logic (video + images)
        LogicResult<Void> mediaResult =
                processCreateMedias(
                        videoUrl,
                        files,
                        event.getMediaList(),
                        event.getTitle(),
                        generatedEventId,
                        ActivityOwnerType.EVENT
                );

        if (mediaResult != null) {
            return mediaResult;
        }

        // Persist final Event with medias
        Event updated = idaoEvent.update(event);

        if (updated == null) {
            return new LogicResult<>("500", "Failed to finalize Event creation", null);
        }

        // Return success
        return new LogicResult<>(
                "201",
                "Event created successfully with uploaded medias",
                null
        );
    }


    /**
     * Retrieve a paginated list of event ordered by start date desc.
     *
     * @param page page index (0-based)
     * @param size number of items per page
     * @return LogicResult containing a page of Event
     */
    public LogicResult<Page<Event>> findEventPage(
            int page,
            int size
    ) {

        // Create pagination information with sorting
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, START_DATE)
        );

        // Call IDAO
        Page<Event> pageResult =
                idaoEvent.findAllByOrderByStartDateDesc(
                        pageable
                );

        // Return an empty page if no data found
        if (pageResult.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    NO_EVENT_FOUND,
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                EVENT_PAGE_RETRIEVED,
                pageResult
        );
    }


    /**
     * Retrieve a paginated list of event according to the visibility (PUBLIC)
     *
     * @param page page index (0-based)
     * @param size number of items per page
     * @return LogicResult containing a page of Event
     */
    public LogicResult<Page<Event>> findAllByVisibilityOrderByStartDateDesc(
            int page,
            int size
    ) {

        // Create pagination information with sorting
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, START_DATE)
        );

        // Call DAO
        Page<Event> pageResult =
                idaoEvent.findAllByVisibilityOrderByStartDateDesc(
                        Visibility.PUBLIC,
                        pageable
                );

        // Return an empty page if no data found
        if (pageResult.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    NO_EVENT_FOUND,
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                EVENT_PAGE_RETRIEVED,
                pageResult
        );
    }


    /**
     * Retrieve a paginated list of event for member space
     *
     * @param page page index (0-based)
     * @param size number of items per page
     * @return LogicResult containing a page of Event
     */
    public LogicResult<Page<Event>> findAllByMemberOnlyVisibilityOrderByStartDateDesc(
            int page,
            int size
    ) {

        // Create pagination information with sorting
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, START_DATE)
        );

        // Call DAO
        Page<Event> pageResult =
                idaoEvent.findAllByVisibilityOrderByStartDateDesc(
                        Visibility.MEMBER_ONLY,
                        pageable
                );

        // Return an empty page if no data found
        if (pageResult.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    NO_EVENT_FOUND,
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                EVENT_PAGE_RETRIEVED,
                pageResult
        );
    }


    /**
     * Retrieve 4 last event (for home page)
     */
    public LogicResult<List<Event>> findLastEvent() {

        List<Event> event = idaoEvent.findAllByOrderByStartDateDesc();

        if (event.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    NO_EVENT_FOUND,
                    List.of()
            );
        }

        return new LogicResult<>(
                "200",
                "Last 4 event retrieved",
                event
        );
    }


    /**
     * Retrieve event by its unique identifier
     */
    public LogicResult<Event> findEventById(Long eventId) {
        if(eventId == null || eventId <= 0) {
            return new LogicResult<>("400", "The event id is required", null);
        }
        Event event = idaoEvent.findById(eventId);
        if(event == null) {
            return new LogicResult<>("404", "Event not found", null);
        }
        return new LogicResult<>("200", "Event retrieved", event);

    }


    /**
     * Updates an existing Event entity and optionally updates its medias.

     * Workflow:
     * - Validate the event identifier
     * - Retrieve the existing Event
     * - Validate and sanitize input DTO
     * - Apply field updates
     * - Update medias if provided
     * - Persist changes
     *
     * @param eventId Identifier of the event to update
     * @param eventDTO DTO containing updated fields
     * @param videoUrl Optional videoUrl to update
     * @param files Optional list of medias to update
     *
     * @return LogicResult indicating success or failure
     */
    @Transactional
    public LogicResult<Void> updateEvent(Long eventId, EventDTO eventDTO, String videoUrl,
                                         List<MultipartFile> files, String removedMediaIdsJson) {
        // Validate the identifier
        if(eventId == null || eventId <= 0) {
            return new LogicResult<>("400", "The event identifier is required", null);
        }
        // Retrieve the existing event
        Event existingEvent = idaoEvent.findById(eventId);
        if(existingEvent == null) {
            return new LogicResult<>("404", "Event not found", null);
        }
        // Prepare a default validation error result
        LogicResult<Void> result = validationError();

        // Validate and sanitize input
        SanitizedActivityInput input = validateAndSanitizeActivity(eventDTO, result);
        if (input == null) {
            // Validation failed
            return result;
        }

        // Apply sanitized updates to the existing entity
        applySanitizedValues(existingEvent, input);

        // Update or delete existing medias
        List<Media> medias = existingEvent.getMediaList();

        if (medias == null) {
            return new LogicResult<>("500",
                    "Event medias not initialized",
                    null);
        }

        /* ******* handle medias ****** */
        // Delegate full media update logic
        LogicResult<Void> mediaResult =
                processUpdateMedias(
                        removedMediaIdsJson,
                        videoUrl,
                        files,
                        existingEvent.getMediaList(),
                        existingEvent.getTitle(),
                        eventId,
                        ActivityOwnerType.EVENT
                );

        if (mediaResult != null) {
            return mediaResult;
        }
        // Persist the updated event
        Event update = idaoEvent.update(existingEvent);
        if(update == null) {
            return new LogicResult<>("500", "Failed to update event", null);
        }
        return new LogicResult<>("200", "Event updated succesfully", null);

    }


    /**
     * Delete event
     */
    @Transactional
    public LogicResult<Void> deleteEvent (Long eventId) {
        if(eventId == null || eventId <= 0) {
            return new LogicResult<>("400", "The event identifier is required", null);
        }
        boolean isDeleted = idaoEvent.deleteById(eventId);
        if(!isDeleted) {
            return new LogicResult<>("404", "Event not deleted", null);
        }
        return new LogicResult<>("200", "Event deleted successfully", null);
    }

}