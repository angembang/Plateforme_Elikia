package fr.elikia.backend.controller;

import fr.elikia.backend.bll.EventService;
import fr.elikia.backend.bo.Event;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.dto.EventDTO;
import fr.elikia.backend.security.jwt.RequiredJWTAuth;
import fr.elikia.backend.security.jwt.RequiredRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST controller responsible for managing Event endpoints.
 */
@RestController
@RequestMapping("/api/event")
@Tag(
        name = "Event",
        description = "Endpoints for managing Event and their associated Media"
)public class EventController {
    // Business service dependency
    private final EventService eventService;

    // Constructor injection
    public EventController(EventService eventService) {

        this.eventService = eventService;
    }

    /**
     * Creates new Event with optional associated Media in a single request.

     * This endpoint:
     * - Delegates all validation to EventService and MediaService
     * - Creates the Event first
     * - Creates and attaches each Media
     * - Stops and returns an error on the first failure
     *
     * @param eventDTO Wrapper DTO containing:
     * - EventDTO (mandatory)
     * - List of MediaDTO (optional)
     * - videoUrl (optional)
     *
     * @return HTTP response containing a LogicResult:
     * - 201 if creation succeeded
     * - 400 if any validation or business error occurred
     */
    @Operation(
            summary = "Create a event with optional media",
            description = "Creates a Event entity and optionally attaches a list of Media to it"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Event created successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation or business error",
            content = @Content(schema = @Schema(implementation = LogicResult.class))
    )
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> addEvent(
            @RequestPart("event") EventDTO eventDTO,
            @RequestPart (value= "videoUrl", required = false) String videoUrl,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {


        // Delegate all validation and business logic to the service
        LogicResult<Void> result =
                eventService.createEvent(eventDTO, videoUrl, files);

        // Map business result code to HTTP status
        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(result);
    }


    /**
     * Retrieve paginated event
     *
     * @param page page index (0-based)
     * @param size number of items per page (default 12)
     * @return paginated list of published event
     */
    @Operation(
            summary = "Retrieve paginated events",
            description = "Returns a paginated list of event"
    )
    @ApiResponse(responseCode = "200", description = "Event page retrieved")
    @GetMapping("/page")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Page<Event>>> findEventPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {

        LogicResult<Page<Event>> result =
                eventService.findEventPage(page, size);

        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(result);
    }


    /**
     * Retrieve paginated event according to the visibility (PUBLIC)
     *
     * @param page page index (0-based)
     * @param size number of items per page (default 12)
     * @return paginated list of event
     */
    @Operation(
            summary = "Retrieve paginated event for public",
            description = "Returns a paginated list event"
    )
    @ApiResponse(responseCode = "200", description = "Event page retrieved")
    @GetMapping("/public/page")
    public ResponseEntity<LogicResult<Page<Event>>> findAllByVisibilityOrderByStartDateDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {

        LogicResult<Page<Event>> result =
                eventService.findAllByVisibilityOrderByStartDateDesc(page, size);

        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(result);
    }


    /**
     * Retrieve paginated event according to the visibility (Member)
     *
     * @param page page index (0-based)
     * @param size number of items per page (default 12)
     * @return paginated list of event
     */
    @Operation(
            summary = "Retrieve paginated event reserved for members",
            description = "Returns a paginated list event"
    )
    @ApiResponse(responseCode = "200", description = "Event page retrieved")
    @GetMapping("/member/page")
    @RequiredJWTAuth
    @RequiredRole("MEMBER")
    public ResponseEntity<LogicResult<Page<Event>>> findAllByMemberOnlyVisibilityOrderByStartDateDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {

        LogicResult<Page<Event>> result =
                eventService.findAllByMemberOnlyVisibilityOrderByStartDateDesc(page, size);

        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(result);
    }


    /**
     * Display the 4th last event for home page
     */
    @Operation(
            summary = "Retrieve the fourth last event",
            description = "Returns the 4th last event"
    )
    @ApiResponse(responseCode = "200", description = "Event retrieved")
    @GetMapping("/latest")
    public ResponseEntity<LogicResult<List<Event>>> findLastEvent() {

        LogicResult<List<Event>> result =
                eventService.findLastEvent();

        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(result);
    }


    /**
     * Show event detail
     *
     * @param eventId the unique identifier of the event
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Retrieve a event by its id",
            description = "Returns the details of a single event"
    )
    @ApiResponse(responseCode = "200", description = "Event retrieved")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @GetMapping("/{eventId}")
    public ResponseEntity<LogicResult<Event>> findEventById(
            @PathVariable Long eventId
    ) {

        LogicResult<Event> result = eventService.findEventById(eventId);

        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(result);
    }


    /**
     * Update event
     *
     * @param eventId the unique identifier of the event, Long eventId,
     *  EventCreationRequest request
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Update a event and its media",
            description = "Updates a Event entity and its associated Media"
    )
    @ApiResponse(responseCode = "200", description = "Event updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @PutMapping(value = "/{eventId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> updateEvent(
            @PathVariable Long eventId,
            @RequestPart("event") EventDTO eventDTO,
            @RequestPart(value = "videoUrl", required = false) String videoUrl,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "removedMediaIds", required = false) String removedMediaIdsJson) {

        LogicResult<Void> result =
                eventService.updateEvent(eventId, eventDTO, videoUrl, files, removedMediaIdsJson);

        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(result);
    }


    /**
     * delete event
     *
     * @param eventId the unique identifier of the event, Long eventId
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Delete a event",
            description = "Deletes a event by its id"
    )
    @ApiResponse(responseCode = "200", description = "Event deleted successfully")
    @ApiResponse(responseCode = "404", description = "Event not found")
    @DeleteMapping("/{eventId}")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> deleteEvent(
            @PathVariable Long eventId
    ) {

        LogicResult<Void> result = eventService.deleteEvent(eventId);

        HttpStatus status = HttpStatus.resolve(
                Integer.parseInt(result.getCode())
        );

        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity
                .status(status)
                .body(result);
    }



}
