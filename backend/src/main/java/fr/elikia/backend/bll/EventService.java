package fr.elikia.backend.bll;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.elikia.backend.bo.*;
import fr.elikia.backend.dao.idao.IDAOEvent;
import fr.elikia.backend.dto.EventDTO;
import fr.elikia.backend.security.InputSanitizer;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for managing Event domain logic.

 * This service handles:
 * - Strong input validation and sanitization (XSS protection)
 * - Business rules for the creation, updating and retrieved of the event
 *
 */
@Service
public class EventService {

    // Dependencies
    private final IDAOEvent idaoEvent;
    private final MediaService mediaService;
    private final ObjectMapper objectMapper;


    public EventService(IDAOEvent idaoEvent, MediaService mediaService, ObjectMapper objectMapper) {
        this.idaoEvent = idaoEvent;
        this.mediaService = mediaService;
        this.objectMapper = objectMapper;
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

        // Validate and sanitize News input
        EventService.SanitizedEventInput input = validateAndSanitizeEvent(eventDTO, result);
        if (input == null) {
            // Validation failed, result already filled
            return result;
        }

        // Create Event entity
        Event event = new Event();
        event.setTitle(input.title());
        event.setDescription(input.description());
        event.setStartDate(input.startDate());
        event.setEndDate(input.endDate());
        event.setLocation(input.location());
        event.setAddress(input.address());
        event.setCapacity(input.capacity());
        event.setVisibility(input.visibility());

        // Persist Event first to generate its identifier
        idaoEvent.create(event);
        Long generatedEventId = event.getEventId();
        if(generatedEventId == null) {
            throw new IllegalStateException("event identifier was not generated");
        }

        /* ********* create media ******** */
        // Create video media if provided
        if (videoUrl != null && !videoUrl.isBlank()) {

            LogicResult<Media> videoResult =
                    mediaService.createMedia(
                            null, // no file
                            videoUrl,
                            event.getTitle(),
                            null,
                            generatedEventId,
                            null,
                            null
                    );

            if (!"201".equals(videoResult.getCode())) {
                return new LogicResult<>(
                        videoResult.getCode(),
                        "Video creation failed: " + videoResult.getMessage(),
                        null
                );
            }
            // Retrieve created Media
            Media videodMedia = videoResult.getData();

            // Attach media to event (bidirectional association)
            event.addMedia(videodMedia);
        }

        // Handle uploaded media files
        if (files != null && !files.isEmpty()) {

            for (MultipartFile file : files) {

                // Delegate full creation to MediaService
                LogicResult<Media> mediaResult =
                        mediaService.createMedia(
                                file,
                                null,
                               event.getTitle(), // Caption
                                null,
                                generatedEventId,
                                null,
                                null
                        );

                // Stop immediately if one media creation fails
                if (!"201".equals(mediaResult.getCode())) {
                    return new LogicResult<>(
                            mediaResult.getCode(),
                            "Media creation failed: " + mediaResult.getMessage(),
                            null
                    );
                }

                // Retrieve created Media
                Media createdMedia = mediaResult.getData();

                // Attach media to event (bidirectional association)
                event.addMedia(createdMedia);
            }
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
                Sort.by(Sort.Direction.DESC, "startDate")
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
                    "No event found",
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                "Event page retrieved",
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
                Sort.by(Sort.Direction.DESC, "startDate")
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
                    "No event found",
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                "Event page retrieved",
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
                Sort.by(Sort.Direction.DESC, "startDate")
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
                    "No event found",
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                "Event page retrieved",
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
                    "No news found",
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
        EventService.SanitizedEventInput input = validateAndSanitizeEvent(eventDTO, result);
        if (input == null) {
            // Validation failed
            return result;
        }

        // Apply sanitized updates to the existing entity
        existingEvent.setTitle(input.title);
        existingEvent.setDescription(input.description);
        existingEvent.setStartDate(input.startDate);
        existingEvent.setEndDate(input.endDate);
        existingEvent.setLocation(input.location);
        existingEvent.setAddress(input.address);
        existingEvent.setCapacity(input.capacity);
        existingEvent.setVisibility(input.visibility);

        // Update or delete existing medias
        List<Media> medias = existingEvent.getMediaList();

        if (medias != null) {
             // ----- Manage video -----

            Media existingVideo = null;

            // Find existing video
            for (Media m : medias) {
                    if (m.getVideoUrl() != null && !m.getVideoUrl().isBlank()) {
                        existingVideo = m;
                        break;
                    }
                }

            // New video provided
            if (videoUrl != null && !videoUrl.isBlank()) {
                if (existingVideo != null) {
                    // Update existing video
                    LogicResult<Void> mediaResult = mediaService.updateMedia(
                            existingVideo.getMediaId(),
                            null,
                            videoUrl,
                            existingEvent.getTitle()
                    );

                    if (!"200".equals(mediaResult.getCode())) {
                        return mediaResult;
                    }
                } else {
                    // Create new video
                    LogicResult<Media> mediaResult = mediaService.createMedia(
                            null,
                            videoUrl,
                            existingEvent.getTitle(),
                            null,
                            eventId,
                            null,
                            null
                    );

                    if (!"201".equals(mediaResult.getCode())) {
                        return new LogicResult<>(
                                mediaResult.getCode(),
                                mediaResult.getMessage(),
                                null
                        );
                    }

                }
            }
            // Delete video
            if ((videoUrl == null || videoUrl.isBlank()) && existingVideo != null) {
                LogicResult<Void> deleteResult =
                        mediaService.deleteMedia(existingVideo.getMediaId());

                if (!"200".equals(deleteResult.getCode())) {
                    return deleteResult;
                }
                medias.remove(existingVideo);
            }
        }

        /* ******* Delete removed medias ****** */
        // Parse removed medias
        List<Long> removedMediaIds = new ArrayList<>();

        if (removedMediaIdsJson != null && !removedMediaIdsJson.isBlank()) {

            try {

                removedMediaIds = objectMapper.readValue(
                        removedMediaIdsJson,
                        new TypeReference<List<Long>>() {}
                );

            } catch (Exception e) {

                return new LogicResult<>("400", "Invalid removedMediaIds", null);
            }
        }

        // Delete removed medias
        if (!removedMediaIds.isEmpty()) {

            List<Long> finalRemovedMediaIds = removedMediaIds;
            if (medias == null) {
                return new LogicResult<>("500", "Event medias not initialized", null);
            }
            medias.removeIf(m ->
                    finalRemovedMediaIds.contains(m.getMediaId())
            );

            for (Long mediaId : removedMediaIds) {

                LogicResult<Void> deleteResult =
                        mediaService.deleteMedia(mediaId);

                if (!"200".equals(deleteResult.getCode())) {
                    return deleteResult;
                }
            }
        }

        // Add new images
        if (files != null && !files.isEmpty()) {

            for (MultipartFile file : files) {

                LogicResult<Media> mediaResult =
                        mediaService.createMedia(
                                file,
                                null,
                                existingEvent.getTitle(),
                                null,
                                eventId,
                                null,
                                null
                        );

                if (!"201".equals(mediaResult.getCode())) {
                    return new LogicResult<>(
                            mediaResult.getCode(),
                            mediaResult.getMessage(),
                            null
                    );
                }
            }
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


    // =========================================================
    // Private update validation methods
    // =========================================================

    /**
     * Validates the event title.
     */
    private boolean isValidTitle(String title, LogicResult<?> result) {

        // Check null or blank
        if (title == null || title.isBlank()) {
            result.setMessage("The title is required");
            return false;
        }

        // Check length constraints
        if (title.length() < 3 || title.length() > 255) {
            result.setMessage("The title must be between 3 and 255 characters");
            return false;
        }

        return true;
    }


    /**
     * Validates the event description.
     */
    private boolean isValidDescription(String description, LogicResult<?> result) {

        // Check null or blank
        if (description == null || description.isBlank()) {
            result.setMessage("The description is required");
            return false;
        }

        // Check length constraints
        if (description.length() < 3 || description.length() > 2000) {
            result.setMessage("The content must be between 3 and 2000 characters");
            return false;
        }

        return true;
    }


    /**
     * Validates the start date.
     * Rule:
     *  Must not be in the past
     *
     */
    private boolean isValidStartDate(LocalDateTime startDate, LogicResult<?> result) {

            // prevent pass start dates
            if (startDate.isBefore(LocalDateTime.now())) {
                result.setMessage("The start date cannot be in the pass");
                return false;
            }

        return true;
    }


    /**
     * Validates the end date.
     * Rule:
     *  Must not be before the start date
     *
     */
    private boolean isValidEndDate(LocalDateTime startDate, LocalDateTime endDate, LogicResult<?> result) {

        if (startDate == null || endDate == null) {

            result.setMessage("Start date and end date are required");
            return false;
        }
        // prevent end dates before start date
        if (endDate.isBefore(startDate)) {
            result.setMessage("The end date cannot be before the start date");
            return false;
        }

        return true;
    }


    /**
     * Validates the event location.
     */
    private boolean isValidLocation(String location, LogicResult<?> result) {

        // Check null or blank
        if (location == null || location.isBlank()) {
            result.setMessage("The location is required");
            return false;
        }

        // Check length constraints
        if (location.length() < 2 || location.length() > 100) {
            result.setMessage("The location must be between 2 and 100 characters");
            return false;
        }

        return true;
    }


    /**
     * Validates the event address.
     */
    private boolean isValidAddress(String address, LogicResult<?> result) {

        // Check null or blank
        if (address == null || address.isBlank()) {
            result.setMessage("The address is required");
            return false;
        }

        // Check length constraints
        if (address.length() < 5 || address.length() > 255) {
            result.setMessage("The address must be between 5 and 255 characters");
            return false;
        }

        // Allow common address characters
        String regex = "^[0-9A-Za-zÀ-ÿ'\\-\\s,.]+$";

        if (!address.matches(regex)) {
            result.setMessage("The address contains invalid characters");
            return false;
        }

        return true;
    }


    /**
     * Validates the event capacity.
     */
    private boolean isValidCapacity(int capacity, LogicResult<?> result) {

        // Must be positive
        if (capacity <= 0) {
            result.setMessage("The capacity must be greater than 0");
            return false;
        }

        // Optional: prevent absurd values
        if (capacity > 50000) {
            result.setMessage("The capacity is too large");
            return false;
        }

        return true;
    }


    /**
     * Validates that the visibility value belongs to the Visibility enum.
     */
    private boolean isValidVisibility(Visibility visibility, LogicResult<?> result) {

        if (visibility == null) {
            result.setMessage("The visibility is required");
            return false;
        }

        return true;
    }


    // =========================================================
    // Helper methods
    // =========================================================
    /**
     * Initial validation error
     */
    private <T> LogicResult<T> validationError() {

        return new LogicResult<>("400", "Validation error", null);
    }


    /**
     * Validates and sanitizes all fields of a EventDTO

     * This method centralizes:
     * - XSS sanitization
     * - Business validation rules

     * If a validation rule fails, the provided LogicResult is filled
     * and the method returns null.
     *
     * @param eventDTO Input DTO
     * @param result Result object used to store validation error messages
     *
     * @return A SanitizedNewsInput object if valid, or null if validation fails
     */
    private EventService.SanitizedEventInput validateAndSanitizeEvent(
           EventDTO eventDTO,
            LogicResult<?> result) {

        // Sanitize user inputs to prevent XSS attacks
        String title = InputSanitizer.sanitize(eventDTO.getTitle());
        String description = InputSanitizer.sanitize(eventDTO.getDescription());
        String location = InputSanitizer.sanitize(eventDTO.getLocation());
        String address = InputSanitizer.sanitize(eventDTO.getAddress());

        // Non-string fields do not require sanitization
        int capacity = eventDTO.getCapacity();
        LocalDateTime startDate = eventDTO.getStartDate();
        LocalDateTime endDate = eventDTO.getEndDate();
        Visibility visibility = eventDTO.getVisibility();


        // Apply validation rules sequentially
        if (!isValidTitle(title, result)) return null;
        if (!isValidDescription(description, result)) return null;
        if (!isValidStartDate(startDate, result)) return null;
        if (!isValidEndDate(startDate, endDate, result)) return null;
        if (!isValidVisibility(visibility, result)) return null;
        if (!isValidLocation(location, result)) return null;
        if (!isValidAddress(address, result)) return null;
        if (!isValidCapacity(capacity, result)) return null;

        // Return a container object with sanitized and validated values
        return new EventService.SanitizedEventInput(
                title,
                description,
                startDate,
                endDate,
                location,
                address,
                capacity,
                visibility
        );
    }


    /**
     * Immutable container for sanitized and validated event input data

     * This record is used as an internal data structure between:
     * - the validation layer
     * - and the business logic of create/update operations

     * Responsibilities:
     * - Hold only trusted, sanitized values
     * - Prevent propagation of raw user input further in the service layer
     * - Guarantee that all fields have passed business validation rules

     * This object is never exposed outside the service layer
     */
    private record SanitizedEventInput(String title,
                                       String description,
                                       LocalDateTime startDate,
                                       LocalDateTime endDate,
                                       String location,
                                       String address,
                                       int capacity,
                                       Visibility visibility) {
    }

}
