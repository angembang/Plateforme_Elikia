package fr.elikia.backend.controller;

import fr.elikia.backend.bll.WorkshopService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Workshop;
import fr.elikia.backend.dto.WorkshopDTO;
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
 * REST controller responsible for managing Workshop endpoints.
 */
@RestController
@RequestMapping("/api/workshop")
@Tag(
        name = "Workshop",
        description = "Endpoints for managing Workshop and their associated Media"
)
public class WorkshopController {
    // Business service dependency
    private final WorkshopService workshopService;

    // Constructor injection
    public WorkshopController(WorkshopService workshopService) {

        this.workshopService = workshopService;
    }


    /**
     * Creates new Workshop with optional associated Media in a single request.

     * This endpoint:
     * - Delegates all validation to WorkshopService and MediaService
     * - Creates the Workshop first
     * - Creates and attaches each Media
     * - Stops and returns an error on the first failure
     *
     * @param workshopDTO Wrapper DTO containing:
     * - WorkshopDTO (mandatory)
     * - List of MediaDTO (optional)
     * - videoUrl (optional)
     *
     * @return HTTP response containing a LogicResult:
     * - 201 if creation succeeded
     * - 400 if any validation or business error occurred
     */
    @Operation(
            summary = "Create a workshop with optional media",
            description = "Creates a Workshop entity and optionally attaches a list of Media to it"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Workshop created successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation or business error",
            content = @Content(schema = @Schema(implementation = LogicResult.class))
    )
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> addWorkshop(
            @RequestPart("workshop") WorkshopDTO workshopDTO,
            @RequestPart (value= "videoUrl", required = false) String videoUrl,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {


        // Delegate all validation and business logic to the service
        LogicResult<Void> result =
                workshopService.createWorkshop(workshopDTO, videoUrl, files);

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
     * Retrieve paginated workshop
     *
     * @param page page index (0-based)
     * @param size number of items per page (default 12)
     * @return paginated list of published workshop
     */
    @Operation(
            summary = "Retrieve paginated workshops",
            description = "Returns a paginated list of workshop"
    )
    @ApiResponse(responseCode = "200", description = "Workshop page retrieved")
    @GetMapping("/page")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Page<Workshop>>> findWorkshopPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {

        LogicResult<Page<Workshop>> result =
                workshopService.findWorkshopPage(page, size);

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
     * Retrieve paginated workshop according to the visibility (PUBLIC)
     *
     * @param page page index (0-based)
     * @param size number of items per page (default 12)
     * @return paginated list of workshop
     */
    @Operation(
            summary = "Retrieve paginated workshop for public",
            description = "Returns a paginated list workshop"
    )
    @ApiResponse(responseCode = "200", description = "Workshop page retrieved")
    @GetMapping("/public/page")
    public ResponseEntity<LogicResult<Page<Workshop>>> findAllByVisibilityOrderByStartDateDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {

        LogicResult<Page<Workshop>> result =
                workshopService.findAllByVisibilityOrderByStartDateDesc(page, size);

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
     * Retrieve paginated workshop according to the visibility (Member)
     *
     * @param page page index (0-based)
     * @param size number of items per page (default 12)
     * @return paginated list of workshop
     */
    @Operation(
            summary = "Retrieve paginated workshop reserved for members",
            description = "Returns a paginated list workshop"
    )
    @ApiResponse(responseCode = "200", description = "Workshop page retrieved")
    @GetMapping("/member/page")
    @RequiredJWTAuth
    @RequiredRole("MEMBER")
    public ResponseEntity<LogicResult<Page<Workshop>>> findAllByMemberOnlyVisibilityOrderByStartDateDesc(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {

        LogicResult<Page<Workshop>> result =
                workshopService.findAllByMemberOnlyVisibilityOrderByStartDateDesc(page, size);

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
     * Display the 4th last workshop for home page
     */
    @Operation(
            summary = "Retrieve the fourth last workshop",
            description = "Returns the 4th last workshop"
    )
    @ApiResponse(responseCode = "200", description = "Workshop retrieved")
    @GetMapping("/latest")
    public ResponseEntity<LogicResult<List<Workshop>>> findLastWorkshop() {

        LogicResult<List<Workshop>> result =
                workshopService.findLastWorkshop();

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
     * Show workshop detail
     *
     * @param workshopId the unique identifier of the workshop
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Retrieve a workshop by its id",
            description = "Returns the details of a single workshop"
    )
    @ApiResponse(responseCode = "200", description = "Workshop retrieved")
    @ApiResponse(responseCode = "404", description = "Workshop not found")
    @GetMapping("/{workshopId}")
    public ResponseEntity<LogicResult<Workshop>> findWorkshopById(
            @PathVariable Long workshopId
    ) {

        LogicResult<Workshop> result = workshopService.findWorkshopById(workshopId);

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
     * Update workshop
     *
     * @param workshopId the unique identifier of the workshop, Long workshopId,
     *  WorkshopCreationRequest request
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Update a workshop and its media",
            description = "Updates a Workshop entity and its associated Media"
    )
    @ApiResponse(responseCode = "200", description = "Workshop updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "Workshop not found")
    @PutMapping(value = "/{workshopId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> updateWorkshop(
            @PathVariable Long workshopId,
            @RequestPart("workshop") WorkshopDTO workshopDTO,
            @RequestPart(value = "videoUrl", required = false) String videoUrl,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            @RequestPart(value = "removedMediaIds", required = false) String removedMediaIdsJson) {

        LogicResult<Void> result =
                workshopService.updateWorkshop(workshopId, workshopDTO, videoUrl, files, removedMediaIdsJson);

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
     * delete workshop
     *
     * @param workshopId the unique identifier of the workshop, Long workshoptId
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Delete a workshop",
            description = "Deletes a workshop by its id"
    )
    @ApiResponse(responseCode = "200", description = "Workshop deleted successfully")
    @ApiResponse(responseCode = "404", description = "Workshop not found")
    @DeleteMapping("/{workshopId}")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> deleteWorkshop(
            @PathVariable Long workshopId
    ) {

        LogicResult<Void> result = workshopService.deleteWorkshop(workshopId);

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