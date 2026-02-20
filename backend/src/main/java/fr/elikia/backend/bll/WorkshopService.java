package fr.elikia.backend.bll;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.elikia.backend.bo.*;
import fr.elikia.backend.bo.enums.ActivityOwnerType;
import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.dao.idao.IDAOWorkshop;
import fr.elikia.backend.dto.WorkshopDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service responsible for managing Workshop domain logic.

 * This service handles:
 * - Strong input validation and sanitization (XSS protection)
 * - Business rules for the creation, updating and retrieved of the event
 *
 */
@Service
public class WorkshopService extends AbstractActivityService {
    public static final String START_DATE = "startDate";
    public static final String NO_WORKSHOP_FOUND = "No workshop found";
    public static final String WORKSHOP_PAGE_RETRIEVED = "Workshop page retrieved";

    // Dependencies
    private final IDAOWorkshop idaoWorkshop;


    public WorkshopService(IDAOWorkshop idaoWorkshop, MediaService mediaService,
                           ObjectMapper objectMapper) {
        super(mediaService, objectMapper);
        this.idaoWorkshop = idaoWorkshop;
    }


    /**
     * Creates a Workshop entity and uploads/attaches multiple Media files to it.

     * Workflow:
     * - Validate and sanitize WorkshopDTO
     * - Create and persist the Workshop entity
     * - For each uploaded file:
     * - Delegate Media creation to MediaService
     * - Attach Media to the Workshop
     * - Persist final Workshop with its medias

     * Business rules:
     * - Event must be created before any Media
     * - Each uploaded file creates exactly one Media
     * - Stop immediately if any Media creation fails
     *
     * @param workshopDTO Data transfer object containing workshop fields
     * @param files Optional list of uploaded media files
     *
     * @return LogicResult indicating success or failure
     */
    @Transactional
    public LogicResult<Void> createWorkshop(
            WorkshopDTO workshopDTO,
            String videoUrl,
            List<MultipartFile> files) {

        // Prepare a default validation error result
        LogicResult<Void> result = validationError();

        // Validate and sanitize workshop input
        SanitizedActivityInput input = validateAndSanitizeActivity(workshopDTO, result);
        if (input == null) {
            // Validation failed, result already filled
            return result;
        }

        // Create Workshop entity
        Workshop workshop = new Workshop();
        applySanitizedValues(workshop, input);

        // Persist Workshop first to generate its identifier
        idaoWorkshop.create(workshop);
        Long generatedWorkshopId = workshop.getWorkshopId();
        if(generatedWorkshopId == null) {
            throw new IllegalStateException("workshop identifier was not generated");
        }

        /* ********* create media ******** */
        // Handle all media logic (video + images)
        LogicResult<Void> mediaResult =
                processCreateMedias(
                        videoUrl,
                        files,
                        workshop.getMediaList(),
                        workshop.getTitle(),
                        generatedWorkshopId,
                        ActivityOwnerType.WORKSHOP
                );

        if (mediaResult != null) {
            return mediaResult;
        }

        // Persist final Workshop with medias
        Workshop updated = idaoWorkshop.update(workshop);

        if (updated == null) {
            return new LogicResult<>("500", "Failed to finalize Workshop creation", null);
        }

        // Return success
        return new LogicResult<>(
                "201",
                "Workshop created successfully with uploaded medias",
                null
        );
    }


    /**
     * Retrieve a paginated list of workshop ordered by start date desc.
     *
     * @param page page index (0-based)
     * @param size number of items per page
     * @return LogicResult containing a page of Workshop
     */
    public LogicResult<Page<Workshop>> findWorkshopPage(
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
        Page<Workshop> pageResult =
                idaoWorkshop.findAllByOrderByStartDateDesc(
                        pageable
                );

        // Return an empty page if no data found
        if (pageResult.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    NO_WORKSHOP_FOUND,
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                WORKSHOP_PAGE_RETRIEVED,
                pageResult
        );
    }


    /**
     * Retrieve a paginated list of workshop according to the visibility (PUBLIC)
     *
     * @param page page index (0-based)
     * @param size number of items per page
     * @return LogicResult containing a page of Workshop
     */
    public LogicResult<Page<Workshop>> findAllByVisibilityOrderByStartDateDesc(
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
        Page<Workshop> pageResult =
                idaoWorkshop.findAllByVisibilityOrderByStartDateDesc(
                        Visibility.PUBLIC,
                        pageable
                );

        // Return an empty page if no data found
        if (pageResult.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    NO_WORKSHOP_FOUND,
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                WORKSHOP_PAGE_RETRIEVED,
                pageResult
        );
    }


    /**
     * Retrieve a paginated list of workshop for member space
     *
     * @param page page index (0-based)
     * @param size number of items per page
     * @return LogicResult containing a page of Workshop
     */
    public LogicResult<Page<Workshop>> findAllByMemberOnlyVisibilityOrderByStartDateDesc(
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
        Page<Workshop> pageResult =
                idaoWorkshop.findAllByVisibilityOrderByStartDateDesc(
                        Visibility.MEMBER_ONLY,
                        pageable
                );

        // Return an empty page if no data found
        if (pageResult.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    NO_WORKSHOP_FOUND,
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                WORKSHOP_PAGE_RETRIEVED,
                pageResult
        );
    }


    /**
     * Retrieve 4 last workshop (for home page)
     */
    public LogicResult<List<Workshop>> findLastWorkshop() {

        List<Workshop> workshop = idaoWorkshop.findAllByOrderByStartDateDesc();

        if (workshop.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    NO_WORKSHOP_FOUND,
                    List.of()
            );
        }

        return new LogicResult<>(
                "200",
                "Last 4 workshop retrieved",
                workshop
        );
    }


    /**
     * Retrieve workshop by its unique identifier
     */
    public LogicResult<Workshop> findWorkshopById(Long workshopId) {
        if(workshopId == null || workshopId <= 0) {
            return new LogicResult<>("400", "The workshop id is required", null);
        }
        Workshop workshop = idaoWorkshop.findById(workshopId);
        if(workshop == null) {
            return new LogicResult<>("404", "Workshop not found", null);
        }
        return new LogicResult<>("200", "Workshop retrieved", workshop);

    }


    /**
     * Updates an existing Workshop entity and optionally updates its medias.

     * Workflow:
     * - Validate the workshop identifier
     * - Retrieve the existing Workshop
     * - Validate and sanitize input DTO
     * - Apply field updates
     * - Update medias if provided
     * - Persist changes
     *
     * @param workshopId Identifier of the workshop to update
     * @param workshopDTO DTO containing updated fields
     * @param videoUrl Optional videoUrl to update
     * @param files Optional list of medias to update
     *
     * @return LogicResult indicating success or failure
     */
    @Transactional
    public LogicResult<Void> updateWorkshop(Long workshopId, WorkshopDTO workshopDTO, String videoUrl,
                                            List<MultipartFile> files, String removedMediaIdsJson) {
        // Validate the identifier
        if(workshopId == null || workshopId <= 0) {
            return new LogicResult<>("400", "The workshop identifier is required", null);
        }
        // Retrieve the existing workshop
        Workshop existingWorkshop = idaoWorkshop.findById(workshopId);
        if(existingWorkshop == null) {
            return new LogicResult<>("404", "Workshop not found", null);
        }
        // Prepare a default validation error result
        LogicResult<Void> result = validationError();

        // Validate and sanitize input
        AbstractActivityService.SanitizedActivityInput input = validateAndSanitizeActivity(workshopDTO, result);
        if (input == null) {
            // Validation failed
            return result;
        }

        // Apply sanitized updates to the existing entity
        applySanitizedValues(existingWorkshop, input);

        // Update or delete existing medias
        List<Media> medias = existingWorkshop.getMediaList();

        if (medias == null) {
            return new LogicResult<>("500",
                    "Workshop medias not initialized",
                    null);
        }

        /* ******* handle medias ****** */
        // Delegate full media update logic
        LogicResult<Void> mediaResult =
                processUpdateMedias(
                        removedMediaIdsJson,
                        videoUrl,
                        files,
                        existingWorkshop.getMediaList(),
                        existingWorkshop.getTitle(),
                        workshopId,
                        ActivityOwnerType.WORKSHOP
                );

        if (mediaResult != null) {
            return mediaResult;
        }

        // Persist the updated event
        Workshop update = idaoWorkshop.update(existingWorkshop);
        if(update == null) {
            return new LogicResult<>("500", "Failed to update workshop", null);
        }
        return new LogicResult<>("200", "Workshop updated succesfully", null);

    }


    /**
     * Delete workshop
     */
    @Transactional
    public LogicResult<Void> deleteWorkshop (Long workshopId) {
        if(workshopId == null || workshopId <= 0) {
            return new LogicResult<>("400", "The workshop identifier is required", null);
        }
        boolean isDeleted = idaoWorkshop.deleteById(workshopId);
        if(!isDeleted) {
            return new LogicResult<>("404", "Workshop not deleted", null);
        }
        return new LogicResult<>("200", "Workshop deleted successfully", null);
    }

}
