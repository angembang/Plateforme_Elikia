package fr.elikia.backend.bll;

import fr.elikia.backend.bo.*;
import fr.elikia.backend.dao.idao.*;
import fr.elikia.backend.security.InputSanitizer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;

/**
 * Service responsible for validating, creating, updating and deleting Media entities.

 * This service handles:
 * - Strong input sanitization (XSS protection)
 * - Validation of images and YouTube videos
 * - Resolution of parent entities (News, Event, Workshop, Achievement)
 * - Attachment of Media to exactly one parent entity
 */
@Service
public class MediaService {
    /** CONSTANTS*/
    // Default title used when no owner title is available
    private static final String DEFAULT_MEDIA_TITLE = "media";
    // Allowed image file extensions
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS =
            Set.of(".jpg", ".jpeg", ".png");
    // Root directory where all uploaded media files are stored
    private static final String MEDIA_STORAGE_ROOT = "/uploads/media";

    // IDAO dependencies used to resolve parent entities
    private final IDAONews idaoNews;
    private final IDAOEvent idaoEvent;
    private final IDAOWorkshop idaoWorkshop;
    private final IDAOAchievement idaoAchievement;
    private final IDAOMedia idaoMedia;


    public MediaService(IDAONews idaoNews,
                        IDAOEvent idaoEvent,
                        IDAOWorkshop idaoWorkshop,
                        IDAOAchievement idaoAchievement, IDAOMedia idaoMedia) {

        this.idaoNews = idaoNews;
        this.idaoEvent = idaoEvent;
        this.idaoWorkshop = idaoWorkshop;
        this.idaoAchievement = idaoAchievement;
        this.idaoMedia = idaoMedia;
    }

    /**
     * Creates a Media entity from an uploaded file and attaches it to exactly one parent entity

     * Responsibilities:
     * - Validate the uploaded file
     * - Enforce ownership rule (exactly one parent)
     * - Generate a safe and unique file name using existing helpers
     * - Store the file physically on disk
     * - Create and persist the Media entity

     * Business rules:
     * - Exactly one owner identifier must be provided
     * - Only JPG, JPEG and PNG images are accepted
     * - A Media is created only if the file is successfully stored
     *
     * @param file Uploaded image file
     * @param caption Optional media caption
     * @param newsId Identifier of the owning News (nullable)
     * @param eventId Identifier of the owning Event (nullable)
     * @param workshopId Identifier of the owning Workshop (nullable)
     * @param achievementId Identifier of the owning Achievement (nullable)
     *
     * @return LogicResult containing the created Media or an error
     */
    public LogicResult<Media> createMedia(MultipartFile file,
                                          String videoUrl,
                                          String caption,
                                          Long newsId,
                                          Long eventId,
                                          Long workshopId,
                                          Long achievementId) {
        // Prepare a default validation error result
        LogicResult<Media> result = validationError();

        // ---------- Retrieve and sanitize caption and video url -----------------
        String sanitizedCaption = caption != null
                ? InputSanitizer.sanitize(caption)
                : null;
        if (isValidCaption(sanitizedCaption, result)) {
            return result;
        }
        String sanitizedVideoUrl = videoUrl != null
                ? InputSanitizer.sanitize(videoUrl)
                : null;
        if (isValidVideoUrl(sanitizedVideoUrl, result)) {
            return result;
        }

        // ------------------ Ownership ----------
        int ownerCount = countOwners(newsId, eventId, workshopId, achievementId);
        if (ownerCount != 1) {
            result.setMessage("A media must be linked to exactly one parent entity");
            return result;
        }

        String ownerTitle = resolveOwnerTitle(
                newsId, eventId, workshopId, achievementId
        );

        // ---------- Image upload ----------
        String imagePath = null;

        if (file != null && !file.isEmpty()) {
            imagePath = storeFileOnDisk(file, ownerTitle, result);
            if (imagePath == null) {
                return result;
            }
        }

        // ---------- Global media validation ----------
        if (isValidMedia(imagePath, sanitizedVideoUrl, result)) {
            return result;
        }

        // ---------- Create entity ----------
        Media media = new Media();
        media.setCaption(sanitizedCaption);
        media.setImagePath(imagePath);
        media.setVideoUrl(sanitizedVideoUrl);

        if (!attachOwner(media, newsId, eventId, workshopId, achievementId, result)) {
            return result;
        }

        Media created = idaoMedia.create(media);
        if (created == null) {
            result.setMessage("Failed to persist Media entity");
            return result;
        }

        result.setCode("201");
        result.setMessage("Media created successfully");
        result.setData(created);

        return result;
    }


    /**
     * Retrieve media by its unique identifier
     */
    public LogicResult<Media> findMediaById(Long mediaId) {
        if(mediaId == null || mediaId <= 0) {
            return new LogicResult<>("400", "The media id is required", null);
        }
        Media media = idaoMedia.findById(mediaId);
        if(media == null) {
            return new LogicResult<>("404", "media not found", null);
        }
        return new LogicResult<>("200", "media retrieved", media);

    }


    /**
     * Update media
     */
    public LogicResult<Void> updateMedia(Long mediaId,
                                         MultipartFile file,
                                         String videoUrl,
                                         String caption) {
        // Validate the identifier
        if(mediaId == null || mediaId <= 0) {
            return new LogicResult<>("400", "The media identifier is required", null);
        }
        // Find existing media
        Media existingMedia = idaoMedia.findById(mediaId);
        if(existingMedia == null) {
            return new LogicResult<>("404", "Media not found", null);
        }

        // Prepare a default validation error result
        LogicResult<Void> result = validationError();

        // ---------- Caption ----------
        if (caption != null) {
            String sanitizedCaption = InputSanitizer.sanitize(caption);
            if (isValidCaption(sanitizedCaption, result)) {
                return result;
            }
            existingMedia.setCaption(sanitizedCaption);
        }

        // ---------- Video URL ----------
        if (videoUrl != null) {
            String sanitizedVideoUrl = InputSanitizer.sanitize(videoUrl);
            if (isValidVideoUrl(sanitizedVideoUrl, result)) {
                return result;
            }
            existingMedia.setVideoUrl(sanitizedVideoUrl);
        }

        // ---------- Resolve existing owner ----------
        Long newsId = null;
        Long eventId = null;
        Long workshopId = null;
        Long achievementId = null;

        if (existingMedia.getNews() != null) {
            newsId = existingMedia.getNews().getNewsId();
        } else if (existingMedia.getEvent() != null) {
            eventId = existingMedia.getEvent().getEventId();
        } else if (existingMedia.getWorkshop() != null) {
            workshopId = existingMedia.getWorkshop().getWorkshopId();
        } else if (existingMedia.getAchievement() != null) {
            achievementId = existingMedia.getAchievement().getAchievementId();
        } else {
            return new LogicResult<>("500",
                    "Media has no parent entity, data integrity error", null);
        }

        String ownerTitle = resolveOwnerTitle(
                newsId, eventId, workshopId, achievementId
        );

        // ---------- Image replacement ----------
        if (file != null && !file.isEmpty()) {
            String newImagePath = storeFileOnDisk(file, ownerTitle, result);
            if (newImagePath == null) {
                return result;
            }
            existingMedia.setImagePath(newImagePath);
        }

        // ---------- Final validation ----------
        if (isValidMedia(existingMedia.getImagePath(),
                existingMedia.getVideoUrl(),
                result)) {
            return result;
        }

        Media updated = idaoMedia.update(existingMedia);
        if (updated == null) {
            return new LogicResult<>("500", "Failed to update media", null);
        }

        return new LogicResult<>("200", "Media updated successfully", null);
    }


    /**
     * Delete media
     */
    public LogicResult<Void> deleteMedia (Long mediaId) {
        if(mediaId == null || mediaId <= 0) {
            return new LogicResult<>("400", "The media identifier is required", null);
        }
        boolean isDeleted = idaoMedia.deleteById(mediaId);
        if(!isDeleted) {
            return new LogicResult<>("404", "Media not deleted", null);
        }
        return new LogicResult<>("200", "Media deleted successfully", null);
    }



    // =========================================================
    // Private validation methods
    // =========================================================
    /**
     * Validates the media caption
     */
    private boolean isValidCaption(String caption, LogicResult<?> result) {
        if (caption == null || caption.isBlank()) {
            result.setMessage("the caption is required");
            return true;
        }
        if (caption.length() < 3 || caption.length() > 200) {
            result.setMessage("The caption must be between 3 and 200 characters");
            return true;
        }
        return false;
    }


    /**
     * Validates a single media according to strict business rules
     */
    private boolean isValidMedia(
            String imagePath,
            String videoUrl,
            LogicResult<?> result) {
        // At least one media source must be provided
        if ((imagePath == null || imagePath.isBlank()) &&
                (videoUrl == null || videoUrl.isBlank())) {
            result.setMessage("A media must contain at least an image or a video");
            return true;
        }
        return false;

    }


    /**
     * Validates the video URL:
     * - HTTPS
     * - YouTube only
     * - Valid format
     */
    private boolean isValidVideoUrl(String videoUrl, LogicResult<?> result) {

        if (videoUrl == null || videoUrl.isBlank()) {
            return false;
        }

        if (!isHttpsUrl(videoUrl, result)) return true;
        return !isValidYouTubeUrl(videoUrl, result);
    }


    // =========================================================
    // Helper (resolve owner title for file name generation
    // =========================================================
    /**
     * Resolves a human-readable title from the parent entity.

     * This title is used to generate meaningful file names.
     */
    private String resolveOwnerTitle(Long newsId,
                                     Long eventId,
                                     Long workshopId,
                                     Long achievementId) {

        if (newsId != null) {
            News news = idaoNews.findById(newsId);
            return news != null ? news.getTitle() : DEFAULT_MEDIA_TITLE;
        }

        if (eventId != null) {
            Event event = idaoEvent.findById(eventId);
            return event != null ? event.getTitle() : DEFAULT_MEDIA_TITLE;
        }

        if (workshopId != null) {
            Workshop workshop = idaoWorkshop.findById(workshopId);
            return workshop != null ? workshop.getTitle() : DEFAULT_MEDIA_TITLE;
        }

        if (achievementId != null) {
            Achievement achievement = idaoAchievement.findById(achievementId);
            return achievement != null ? achievement.getTitle() : DEFAULT_MEDIA_TITLE;
        }

        return DEFAULT_MEDIA_TITLE;
    }


    /**
     * Generate a safe file name
     */
    private String generateSafeFileName(String title, String originalFileName) {

        String baseName = title
                .toLowerCase()
                .replaceAll("[^a-z0-9 ]", "")
                .trim()
                .replaceAll("\\s+", "-");

        String extension = originalFileName.substring(
                originalFileName.lastIndexOf(".")
        );

        long timestamp = System.currentTimeMillis();

        return baseName + "-" + timestamp + extension;
    }


    /**
     * Initial validation error
     */
    private <T> LogicResult<T> validationError() {

        return new LogicResult<>("400", "Validation error", null);
    }


    /**
     * Immutable container for sanitized and validated Media input data.

     * This record is used internally to transport only trusted values
     * from the validation layer to the business logic.

     * Responsibilities:
     * - Hold only sanitized user input
     * - Guarantee that all business validation rules have passed
     * - Avoid propagating raw DTO data inside the service

     * This object is never exposed outside the MediaService.
     */
    private record SanitizedMediaInput(// Sanitized and validated media caption.
                                      String caption,
                                      // Sanitized and validated image path (maybe null)
                                      String imagePath,
                                      // Sanitized and validated video URL (maybe null)
                                       String videoUrl) {
        // Immutable data carrier only
    }


    /**
     * Checks that the video URL uses HTTPS and has a valid length.
     */
    private boolean isHttpsUrl(String videoUrl, LogicResult<?> result) {

        if (!videoUrl.startsWith("https://")) {
            result.setMessage("Video URL must use HTTPS");
            return false;
        }

        if (videoUrl.length() > 500) {
            result.setMessage("Video URL is too long");
            return false;
        }

        return true;
    }


    /**
     * Checks that the video URL is a valid YouTube URL.
     */
    private boolean isValidYouTubeUrl(String videoUrl, LogicResult<?> result) {

        try {
            URI uri = URI.create(videoUrl);
            String host = uri.getHost();

            if (host == null) {
                result.setMessage("Invalid video URL");
                return false;
            }

            host = host.toLowerCase();

            boolean isYouTubeHost =
                    host.equals("www.youtube.com") ||
                            host.equals("youtube.com") ||
                            host.equals("youtu.be");

            if (!isYouTubeHost) {
                result.setMessage("Only YouTube video links are allowed");
                return false;
            }

            if (host.equals("youtu.be")) {
                return isValidShortYouTubeLink(uri, result);
            } else {
                return isValidWatchYouTubeLink(uri, result);
            }

        } catch (IllegalArgumentException e) {
            result.setMessage("Invalid video URL format");
            return false;
        }
    }


    /**
     * Validates a YouTube short link (YouTube format)
     * Business rules:
     * - The path must not be null
     * - The path must contain a video identifier (length > 1)
     * Example of valid format:
     * <a href="https://youtu.be/VIDEO_ID">...</a>
     *
     * @param uri Parsed URI of the video URL
     * @param result Result object used to store validation error messages
     *
     * @return true if the short YouTube link is valid, false otherwise
     */
    private boolean isValidShortYouTubeLink(URI uri, LogicResult<?> result) {

        // The path must exist and contain at least one character after '/'
        if (uri.getPath() == null || uri.getPath().length() <= 1) {
            result.setMessage("Invalid YouTube short link");
            return false;
        }

        return true;
    }



    /**
     * Validates a standard YouTube watch URL (youtube.com/watch format).
     * Business rules:
     * - The query string must exist
     * - The query string must contain a "v=" parameter (video identifier)
     * Example of valid format:
     * <a href="https://www.youtube.com/watch?v=VIDEO_ID">...</a>
     *
     * @param uri Parsed URI of the video URL
     * @param result Result object used to store validation error messages
     *
     * @return true if the watch YouTube link is valid, false otherwise
     */
    private boolean isValidWatchYouTubeLink(URI uri, LogicResult<?> result) {

        // Extract query parameters from the URL
        String query = uri.getQuery();

        // The query must exist and contain the "v=" parameter
        if (query == null || !query.contains("v=")) {
            result.setMessage("Invalid YouTube watch URL");
            return false;
        }

        return true;
    }


    /**
     * Counts how many parent entity identifiers are provided.

     * This method is used to enforce the business rule:
     * - A Media must be linked to exactly one parent entity.
     *
     * @param newsId Identifier of the News entity (maybe null)
     * @param eventId Identifier of the Event entity (maybe null)
     * @param workshopId Identifier of the Workshop entity (maybe null)
     * @param achievementId Identifier of the Achievement entity (maybe null)
     *
     * @return The number of non-null parent identifiers
     */
    private int countOwners(Long newsId,
                            Long eventId,
                            Long workshopId,
                            Long achievementId) {

        int count = 0;
        // Increment the counter for each non-null owner ID
        if (newsId != null) count++;
        if (eventId != null) count++;
        if (workshopId != null) count++;
        if (achievementId != null) count++;
        return count;
    }


    /**
     * Resolves and attaches the Media entity to its unique parent entity

     * Business rules:
     * - Exactly one parent identifier must be non-null
     * - The referenced parent entity must exist in the database
     * - The Media must be attached using the appropriate bidirectional association

     * This method centralizes the owner resolution logic in order to:
     * - Reduce cognitive complexity in the service layer
     * - Guarantee a single point of truth for owner attachment
     *
     * @param media Media entity to attach
     * @param newsId Identifier of the News entity (maybe null)
     * @param eventId Identifier of the Event entity (maybe null)
     * @param workshopId Identifier of the Workshop entity (maybe null)
     * @param achievementId Identifier of the Achievement entity (maybe null)
     * @param result Result object used to store validation error messages
     *
     * @return true if the Media was successfully attached to its parent, false otherwise
     */
    private boolean attachOwner(
            Media media,
            Long newsId,
            Long eventId,
            Long workshopId,
            Long achievementId,
            LogicResult<?> result) {

        // Attach to News if provided
        if (newsId != null) {
            News news = idaoNews.findById(newsId);
            if (news == null) {
                result.setMessage("No news found ");
                return false;
            }
            news.addMedia(media);
            return true;
        }

        // Attach to Event if provided
        if (eventId != null) {
            Event event = idaoEvent.findById(eventId);
            if (event == null) {
                result.setMessage("No Event found");
                return false;
            }
            event.addMedia(media);
            return true;
        }

        // Attach to Workshop if provided
        if (workshopId != null) {
            Workshop workshop = idaoWorkshop.findById(workshopId);
            if (workshop == null) {
                result.setMessage("No workshop found");
                return false;
            }
            workshop.addMedia(media);
            return true;
        }

        // Attach to Achievement if provided
        if (achievementId != null) {
            Achievement achievement = idaoAchievement.findById(achievementId);
            if (achievement == null) {
                result.setMessage("No achievement found");
                return false;
            }
            achievement.addMedia(media);
            return true;
        }

        // Should never happen because ownerCount was already checked
        result.setMessage("A media must be linked to exactly one parent entity");
        return false;
    }


    private String storeFileOnDisk(MultipartFile file,
                                   String ownerTitle,
                                   LogicResult<?> result) {

        if (file == null || file.isEmpty()) {
            result.setMessage("Uploaded file is required");
            return null;
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isBlank()) {
            result.setMessage("Invalid file name");
            return null;
        }

        String lowerFileName = originalFileName.toLowerCase();

        boolean validExtension = ALLOWED_IMAGE_EXTENSIONS
                .stream()
                .anyMatch(lowerFileName::endsWith);

        if (!validExtension) {
            result.setMessage("Only JPG, JPEG and PNG images are allowed");
            return null;
        }

        String generatedFileName =
                generateSafeFileName(ownerTitle, originalFileName);

        try {
            Path storageDirectory = Paths.get(MEDIA_STORAGE_ROOT);
            Files.createDirectories(storageDirectory);

            Files.copy(
                    file.getInputStream(),
                    storageDirectory.resolve(generatedFileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            return generatedFileName;

        } catch (IOException e) {
            result.setMessage("Failed to store media file on disk");
            return null;
        }
    }
}