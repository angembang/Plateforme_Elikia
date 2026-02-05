package fr.elikia.backend.bll;

import fr.elikia.backend.bo.*;
import fr.elikia.backend.dao.idao.IDAONews;
import fr.elikia.backend.dto.NewsDTO;
import fr.elikia.backend.security.InputSanitizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service responsible for managing News domain logic.

 * This service handles:
 * - Strong input validation and sanitization (XSS protection)
 * - Business rules for News creation
 * - Persistence of News and its associated Media
 */
@Service
public class NewsService {
    // Dependencies
    private final IDAONews idaoNews;
    private final MediaService mediaService;


    public NewsService(IDAONews idaoNews, MediaService mediaService) {
        this.idaoNews = idaoNews;
        this.mediaService = mediaService;
    }

    /**
     * Creates a News entity and uploads/attaches multiple Media files to it.

     * Workflow:
     * - Validate and sanitize NewsDTO
     * - Create and persist the News entity
     * - For each uploaded file:
     * - Delegate Media creation to MediaService
     * - Attach Media to the News
     * - Persist final News with its medias

     * Business rules:
     * - News must be created before any Media
     * - Each uploaded file creates exactly one Media
     * - Stop immediately if any Media creation fails
     *
     * @param newsDTO Data transfer object containing news fields
     * @param files Optional list of uploaded media files
     *
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> createNews(
            NewsDTO newsDTO,
            List<MultipartFile> files) {

        // Prepare a default validation error result
        LogicResult<Void> result = validationError();

        // Validate and sanitize News input
        SanitizedNewsInput input = validateAndSanitizeNews(newsDTO, result);
        if (input == null) {
            // Validation failed, result already filled
            return result;
        }

        // Create News entity
        News news = new News();
        news.setTitle(input.title());
        news.setContent(input.content());
        ContentStatus status = input.contentStatus();
        news.setContentStatus(status);
        news.setVisibility(input.visibility());
        if (status == ContentStatus.PUBLISHED) {
            // Immediate publication
            news.setPublishedAt(LocalDateTime.now());
        } else {
            // status CREATED (Planning the publication)
            news.setPublishedAt(input.publishedAt());
        }

        // Persist News first to generate its identifier
        idaoNews.create(news);
        Long generatedNewsId = news.getNewsId();
        if(generatedNewsId == null) {
            throw new IllegalStateException("news identifier was not generated");
        }

        // Handle uploaded media files
        if (files != null && !files.isEmpty()) {

            for (MultipartFile file : files) {

                // Delegate full creation to MediaService
                LogicResult<Media> mediaResult =
                        mediaService.createMedia(
                                file,
                                null, // videoUrl
                                news.getTitle(), // caption
                                generatedNewsId,
                                null,
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

                // Attach media to news (bidirectional association)
                news.addMedia(createdMedia);
            }
        }

        // Persist final News with medias
        News updated = idaoNews.update(news);

        if (updated == null) {
            return new LogicResult<>("500", "Failed to finalize News creation", null);
        }

        // Return success
        return new LogicResult<>(
                "201",
                "News created successfully with uploaded medias",
                null
        );
    }


    /**
     * Retrieve all news order by desc
     */
    public LogicResult<List<News>> findAllNews() {
        List<News> allNews = idaoNews.findAllDesc();
        if(allNews == null || allNews.isEmpty()) {
            return new LogicResult<>("200", "No News retrieved", List.of());
        }
        return new LogicResult<>("200", "Published news retrieved", allNews);

    }


    /**
     * Retrieve all news which have PUBLISHED contentStatus
     */
    public LogicResult<List<News>> findAllPublishedNews() {
        List<News> allPublishedNews = idaoNews.findAllPublishedNews(ContentStatus.PUBLISHED);
        if(allPublishedNews == null || allPublishedNews.isEmpty()) {
            return new LogicResult<>("200", "No published news retrieved", List.of());
        }
        return new LogicResult<>("200", "Published news retrieved", allPublishedNews);

    }


    /**
     * Retrieve last published news (for home page)
     */
    public LogicResult<List<News>> findLastPublishedNews(int limit) {

        List<News> news = idaoNews.findLastPublishedNews(limit);

        if (news.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    "No news found",
                    List.of()
            );
        }

        return new LogicResult<>(
                "200",
                "Last published news retrieved",
                news
        );
    }


    /**
     * Retrieve a paginated list of published news.
     *
     * @param page page index (0-based)
     * @param size number of items per page
     * @return LogicResult containing a page of News
     */
    public LogicResult<Page<News>> findPublishedNewsPage(
            int page,
            int size
    ) {

        // Create pagination information with sorting
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "publishedAt")
        );

        // Call DAO instead of repository
        Page<News> pageResult =
                idaoNews.findPublishedNewsPage(
                        ContentStatus.PUBLISHED,
                        pageable
                );

        // Return an empty page if no data found
        if (pageResult.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    "No published news found",
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                "Published news page retrieved",
                pageResult
        );
    }


    /**
     * Retrieve a paginated list of published news according to the visibility (PUBLIC || MEMBER_ONLY)
     *
     * @param page page index (0-based)
     * @param size number of items per page
     * @return LogicResult containing a page of News
     */
    public LogicResult<Page<News>> findAllByContentStatusAndVisibilityAfterOrderByPublishedAtDesc(
            int page,
            int size
    ) {

        // Create pagination information with sorting
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "publishedAt")
        );

        // Call DAO instead of repository
        Page<News> pageResult =
                idaoNews.findAllByContentStatusAndVisibilityAfterOrderByPublishedAtDesc(
                        ContentStatus.PUBLISHED,
                        Visibility.MEMBER_ONLY,
                        pageable
                );

        // Return an empty page if no data found
        if (pageResult.isEmpty()) {
            return new LogicResult<>(
                    "200",
                    "No published news found",
                    Page.empty(pageable)
            );
        }

        // Successful response
        return new LogicResult<>(
                "200",
                "Published news page retrieved",
                pageResult
        );
    }


    /**
     * Retrieve news by its unique identifier
     */
    public LogicResult<News> findNewsById(Long newsId) {
        if(newsId == null || newsId <= 0) {
            return new LogicResult<>("400", "The news id is required", null);
        }
        News news = idaoNews.findById(newsId);
        if(news == null) {
            return new LogicResult<>("404", "News not found", null);
        }
        return new LogicResult<>("200", "News retrieved", news);

    }


    /**
     * Updates an existing News entity and optionally updates its medias.

     * Workflow:
     * - Validate the news identifier
     * - Retrieve the existing News
     * - Validate and sanitize input DTO
     * - Apply field updates
     * - Update medias if provided
     * - Persist changes
     *
     * @param newsId Identifier of the news to update
     * @param newsDTO DTO containing updated fields
     * @param files Optional list of medias to update
     *
     * @return LogicResult indicating success or failure
     */
    public LogicResult<Void> updateNews(Long newsId, NewsDTO newsDTO,  List<MultipartFile> files) {
        // Validate the identifier
        if(newsId == null || newsId <= 0) {
            return new LogicResult<>("400", "The news identifier is required", null);
        }
        // Retrieve the existing news
        News existingNews = idaoNews.findById(newsId);
        if(existingNews == null) {
            return new LogicResult<>("404", "News not found", null);
        }
        // Prepare a default validation error result
        LogicResult<Void> result = validationError();

        // Validate and sanitize input
        SanitizedNewsInput input = validateAndSanitizeNews(newsDTO, result);
        if (input == null) {
            // Validation failed
            return result;
        }

        // Apply sanitized updates to the existing entity
        existingNews.setTitle(input.title);
        existingNews.setContent(input.content);
        existingNews.setPublishedAt(input.publishedAt);
        existingNews.setVisibility(input.visibility);
        existingNews.setContentStatus(input.contentStatus);

        // Update medias if provided
        if (files != null && !files.isEmpty()) {

            LogicResult<Void> mediaResult =
                    updateMediasForNews(
                            existingNews,
                            files,
                            existingNews.getTitle() // Default caption

                    );

                if (!"201".equals(mediaResult.getCode())) {
                    return mediaResult;
                }

        }

        // Persist the updated news
        News update = idaoNews.update(existingNews);
        if(update == null) {
            return new LogicResult<>("500", "Failed to update news", null);
        }
        return new LogicResult<>("200", "News updated succesfully", null);

    }


    /**
     * Delete news
     */
    public LogicResult<Void> deleteNews (Long newsId) {
        if(newsId == null || newsId <= 0) {
            return new LogicResult<>("400", "The news identifier is required", null);
        }
        boolean isDeleted = idaoNews.deleteById(newsId);
        if(!isDeleted) {
            return new LogicResult<>("404", "News not deleted", null);
        }
        return new LogicResult<>("200", "News deleted successfully", null);
    }


    // =========================================================
    // Private validation methods
    // =========================================================

    /**
     * Validates the news title.
     */
    private boolean isValidTitle(String title, LogicResult<?> result) {

        // Check null or blank
        if (title == null || title.isBlank()) {
            result.setMessage("The title is required");
            return false;
        }

        // Check length constraints
        if (title.length() < 3 || title.length() > 150) {
            result.setMessage("The title must be between 3 and 150 characters");
            return false;
        }

        return true;
    }

    /**
     * Validates the news content.
     */
    private boolean isValidContent(String content, LogicResult<?> result) {

        // Check null or blank
        if (content == null || content.isBlank()) {
            result.setMessage("The content is required");
            return false;
        }

        // Check length constraints
        if (content.length() < 3 || content.length() > 5000) {
            result.setMessage("The content must be between 3 and 5000 characters");
            return false;
        }

        return true;
    }

    /**
     * Validates the publication date.

     * Rule:
     *  Must not be in the past
     */
    private boolean isValidPublishedAt(LocalDateTime publishedAt, ContentStatus contentStatus,LogicResult<?> result) {

        if (contentStatus == ContentStatus.CREATED) {

            if (publishedAt == null) {
                result.setMessage("Publication date is required when status is CREATED");
                return false;
            }
            // prevent pass publication dates
            if (publishedAt.isBefore(LocalDateTime.now())) {
                result.setMessage("The publication date cannot be in the pass");
                return false;
            }
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

    /**
     * Validates that the content status value belongs to the ContentStatus enum.
     */
    private boolean isValidContentStatus(ContentStatus contentStatus, LogicResult<?> result) {

        if (contentStatus == null) {
            result.setMessage("The content status is required");
            return false;
        }

        return true;
    }


    // =========================================================
    // Private update validation methods
    // =========================================================
    /**
     * Validates and sanitizes all fields of a NewsDTO

     * This method centralizes:
     * - XSS sanitization
     * - Business validation rules

     * If a validation rule fails, the provided LogicResult is filled
     * and the method returns null.
     *
     * @param newsDTO Input DTO
     * @param result Result object used to store validation error messages
     *
     * @return A SanitizedNewsInput object if valid, or null if validation fails
     */
    private SanitizedNewsInput validateAndSanitizeNews(
            NewsDTO newsDTO,
            LogicResult<?> result) {

        // Sanitize user inputs to prevent XSS attacks
        String title = InputSanitizer.sanitize(newsDTO.getTitle());
        String content = InputSanitizer.sanitize(newsDTO.getContent());

        // Non-string fields do not require sanitization
        LocalDateTime publishedAt = newsDTO.getPublishedAt();
        Visibility visibility = newsDTO.getVisibility();
        ContentStatus contentStatus = newsDTO.getContentStatus();

        // Apply validation rules sequentially
        if (!isValidTitle(title, result)) return null;
        if (!isValidContent(content, result)) return null;
        if (!isValidPublishedAt(publishedAt, contentStatus, result)) return null;
        if (!isValidVisibility(visibility, result)) return null;
        if (!isValidContentStatus(contentStatus, result)) return null;

        // Return a container object with sanitized and validated values
        return new SanitizedNewsInput(
                title,
                content,
                publishedAt,
                visibility,
                contentStatus
        );
    }


    /**
     * Updates all medias attached to news

     * Business rules:
     * - The news must already have medias
     * - The number of medias to update must match the existing ones
     * - Each media is updated individually via MediaService
     *
     * @param existingNews the owning news
     * @param files List of medias to update
     * @param caption the caption of the media
     *
     * @return LogicResult indicating success or failure
     */
    private LogicResult<Void> updateMediasForNews(
            News existingNews,
            List<MultipartFile> files,
            String caption) {

        List<Media> existingMedias = existingNews.getMediaList();

        // --- No existing medias ---
        if (existingMedias == null || existingMedias.isEmpty()) {
            return new LogicResult<>("400",
                    "The news has no media to update",
                    null);
        }

        // --- Defensive check ---
        if (files.size() != existingMedias.size()) {
            return new LogicResult<>("400",
                    "The number of uploaded files does not match existing medias",
                    null);
        }

        // --- Update medias one by one ---
        for (int i = 0; i < files.size(); i++) {

            MultipartFile file = files.get(i);
            Media existingMedia = existingMedias.get(i);
            Long mediaId = existingMedia.getMediaId();

            LogicResult<Void> mediaResult =
                    mediaService.updateMedia(
                            mediaId,
                            file,
                            null,
                            caption
                    );

            if (!"200".equals(mediaResult.getCode())) {
                return new LogicResult<>(
                        mediaResult.getCode(),
                        "Media update failed: " + mediaResult.getMessage(),
                        null
                );
            }
        }

        return new LogicResult<>("200", "Medias updated successfully", null);
    }


    /**
     * Initial validation error
     */
    private <T> LogicResult<T> validationError() {

        return new LogicResult<>("400", "Validation error", null);
    }


    /**
     * Immutable container for sanitized and validated News input data

     * This record is used as an internal data structure between:
     * - the validation layer
     * - and the business logic of create/update operations

     * Responsibilities:
     * - Hold only trusted, sanitized values
     * - Prevent propagation of raw user input further in the service layer
     * - Guarantee that all fields have passed business validation rules

     * This object is never exposed outside the service layer
     */
    private record SanitizedNewsInput(String title, String content,
                                      LocalDateTime publishedAt,
                                      Visibility visibility,
                                      ContentStatus contentStatus) {
    }

}
