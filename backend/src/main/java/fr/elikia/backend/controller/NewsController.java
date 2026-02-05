package fr.elikia.backend.controller;

import fr.elikia.backend.bll.NewsService;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.News;
import fr.elikia.backend.dto.NewsDTO;
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
 * REST controller responsible for managing News endpoints.
 */
@RestController
@RequestMapping("api/news")
@Tag(
        name = "News",
        description = "Endpoints for managing News and their associated Media"
)
public class NewsController {
    // Business service dependency
    private final NewsService newsService;

    // Constructor injection
    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    /**
     * Creates new News with optional associated Media in a single request.

     * This endpoint:
     * - Delegates all validation to NewsService and MediaService
     * - Creates the News first
     * - Creates and attaches each Media
     * - Stops and returns an error on the first failure
     *
     * @param newsDTO Wrapper DTO containing:
     * - NewsDTO (mandatory)
     * - List of MediaDTO (optional)
     *
     * @return HTTP response containing a LogicResult:
     * - 201 if creation succeeded
     * - 400 if any validation or business error occurred
     */
    @Operation(
            summary = "Create a news with optional media",
            description = "Creates a News entity and optionally attaches a list of Media to it"
    )
    @ApiResponse(
            responseCode = "201",
            description = "News created successfully"
    )
    @ApiResponse(
            responseCode = "400",
            description = "Validation or business error",
            content = @Content(schema = @Schema(implementation = LogicResult.class))
    )
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> addNews(
            @RequestPart("news") NewsDTO newsDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {


        // Delegate all validation and business logic to the service
        LogicResult<Void> result =
                newsService.createNews(newsDTO, files);

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
     * Display all news with Medias associated
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Retrieve all news",
            description = "Returns the list of all news"
    )
    @ApiResponse(
            responseCode = "200",
            description = "News retrieved successfully"
    )
    @GetMapping("/management")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<List<News>>> findAllNews() {

        LogicResult<List<News>> result = newsService.findAllNews();

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
     * Display all published news with Medias associated
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Retrieve all published news",
            description = "Returns the list of all news which have the content status published"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Published news retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<LogicResult<List<News>>> findAllPublishedNews() {

        LogicResult<List<News>> result = newsService.findAllPublishedNews();

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
     * Display last published news for home page
     */
    @Operation(
            summary = "Retrieve last published news",
            description = "Returns the last published news limited by count"
    )
    @ApiResponse(responseCode = "200", description = "News retrieved")
    @GetMapping("/latest")
    public ResponseEntity<LogicResult<List<News>>> findLastPublishedNews(
            @RequestParam(defaultValue = "4") int limit
    ) {

        LogicResult<List<News>> result =
                newsService.findLastPublishedNews(limit);

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
     * Retrieve paginated published news.
     *
     * @param page page index (0-based)
     * @param size number of items per page (default 12)
     * @return paginated list of published news
     */
    @Operation(
            summary = "Retrieve paginated published news",
            description = "Returns a paginated list of published news"
    )
    @ApiResponse(responseCode = "200", description = "News page retrieved")
    @GetMapping("/page")
    public ResponseEntity<LogicResult<Page<News>>> findPublishedNewsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {

        LogicResult<Page<News>> result =
                newsService.findPublishedNewsPage(page, size);

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
     * Show news detail
     *
     * @param newsId the unique identifier of the news
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Retrieve a news by its id",
            description = "Returns the details of a single news"
    )
    @ApiResponse(responseCode = "200", description = "News retrieved")
    @ApiResponse(responseCode = "404", description = "News not found")
    @GetMapping("/{newsId}")
    public ResponseEntity<LogicResult<News>> findNewsById(
            @PathVariable Long newsId
    ) {

        LogicResult<News> result = newsService.findNewsById(newsId);

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
     * Update news
     *
     * @param newsId the unique identifier of the news, Long newsId,
     *  NewsCreationRequest request
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Update a news and its media",
            description = "Updates a News entity and its associated Media"
    )
    @ApiResponse(responseCode = "200", description = "News updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation error")
    @ApiResponse(responseCode = "404", description = "News not found")
    @PutMapping(value = "/{newsId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> updateNews(
            @PathVariable Long newsId,
            @RequestPart("news") NewsDTO newsDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        LogicResult<Void> result =
                newsService.updateNews(newsId, newsDTO, files);

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
     * delete news
     *
     * @param newsId the unique identifier of the news, Long newsId
     *
     * @return ResponseEntity
     */
    @Operation(
            summary = "Delete a news",
            description = "Deletes a news by its id"
    )
    @ApiResponse(responseCode = "200", description = "News deleted successfully")
    @ApiResponse(responseCode = "404", description = "News not found")
    @DeleteMapping("/{newsId}")
    @RequiredJWTAuth
    @RequiredRole("ADMIN")
    public ResponseEntity<LogicResult<Void>> deleteNews(
            @PathVariable Long newsId
    ) {

        LogicResult<Void> result = newsService.deleteNews(newsId);

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
