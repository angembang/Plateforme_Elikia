package fr.elikia.backend.bll;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.elikia.backend.bo.ActivityEntity;
import fr.elikia.backend.bo.LogicResult;
import fr.elikia.backend.bo.Media;
import fr.elikia.backend.bo.enums.ActivityOwnerType;
import fr.elikia.backend.bo.enums.Visibility;
import fr.elikia.backend.dto.ActivityDTO;
import fr.elikia.backend.security.InputSanitizer;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract service containing common business logic
 * shared between Event and Workshop services.
 */
public abstract class AbstractActivityService {
    protected final MediaService mediaService;
    protected final ObjectMapper objectMapper;

    protected AbstractActivityService(
            MediaService mediaService,
            ObjectMapper objectMapper
    ) {
        this.mediaService = mediaService;
        this.objectMapper = objectMapper;
    }

    /**
     * Parses removed media IDs from JSON string.
     */
    protected List<Long> parseRemovedMediaIds(String json) {

        try {
            if (json == null || json.isBlank()) {
                return new ArrayList<>();
            }

            return objectMapper.readValue(
                    json,
                    new TypeReference<List<Long>>() {}
            );

        } catch (Exception e) {
            return null;
        }
    }


    /**
     * Deletes removed medias.
     */
    protected LogicResult<Void> deleteRemovedMedias(
            List<Long> removedIds,
            List<Media> medias
    ) {

        if (removedIds == null || removedIds.isEmpty()) {
            return null;
        }

        medias.removeIf(m -> removedIds.contains(m.getMediaId()));

        for (Long id : removedIds) {

            LogicResult<Void> result =
                    mediaService.deleteMedia(id);

            if (!"200".equals(result.getCode())) {
                return result;
            }
        }

        return null;
    }


    /**
     * Handles video creation, update or deletion.
     */
    protected LogicResult<Void> handleVideo(
            String videoUrl,
            List<Media> medias,
            String title,
            Long activityId,
            ActivityOwnerType ownerType
    ) {

        Media existingVideo = null;

        for (Media media : medias) {
            if (media.getVideoUrl() != null &&
                    !media.getVideoUrl().isBlank()) {
                existingVideo = media;
                break;
            }
        }

        // Create or update
        if (videoUrl != null && !videoUrl.isBlank()) {

            if (existingVideo != null) {

                LogicResult<Void> result = mediaService.updateMedia(
                        existingVideo.getMediaId(),
                        null,
                        videoUrl,
                        title
                );
                if (!"200".equals(result.getCode())) {
                    return result;
                }

            } else {

                LogicResult<Media> create;
                if (ownerType == ActivityOwnerType.WORKSHOP) {
                    create = mediaService.createMedia(
                            null,
                            videoUrl,
                            title,
                            null,
                            null,
                            activityId,
                            null
                    );
                    if (!"201".equals(create.getCode())) {
                        return new LogicResult<>(
                                create.getCode(),
                                create.getMessage(),
                                null
                        );
                    }

                    medias.add(create.getData());

                } else if (ownerType == ActivityOwnerType.EVENT) {
                    create = mediaService.createMedia(
                            null,
                            videoUrl,
                            title,
                            null,
                            activityId,
                            null,
                            null
                    );
                    if (!"201".equals(create.getCode())) {
                        return new LogicResult<>(
                                create.getCode(),
                                create.getMessage(),
                                null
                        );
                    }

                    medias.add(create.getData());
                } else {
                    create = mediaService.createMedia(
                            null,
                            videoUrl,
                            title,
                            null,
                            null,
                            null,
                            activityId
                    );
                    if (!"201".equals(create.getCode())) {
                        return new LogicResult<>(
                                create.getCode(),
                                create.getMessage(),
                                null
                        );
                    }

                    medias.add(create.getData());

                }

            }
        }

        // Delete
        if ((videoUrl == null || videoUrl.isBlank())
                && existingVideo != null) {

            LogicResult<Void> delete =
                    mediaService.deleteMedia(
                            existingVideo.getMediaId()
                    );

            if (!"200".equals(delete.getCode())) {
                return delete;
            }

            medias.remove(existingVideo);
        }

        return null;
    }


    /**
     * Handles new uploaded images.
     */
    protected LogicResult<Void> handleNewImages(
            List<MultipartFile> files,
            List<Media> medias,
            String title,
            Long activityId,
            ActivityOwnerType ownerType
    ) {

        if (files == null || files.isEmpty()) {
            return null;
        }

        for (MultipartFile file : files) {

            LogicResult<Media> result;
            if (ownerType == ActivityOwnerType.WORKSHOP) {
                result = mediaService.createMedia(
                        file,
                        null,
                        title,
                        null,
                        null,
                        activityId,
                        null
                );
                if (!"201".equals(result.getCode())) {
                    return new LogicResult<>(
                            result.getCode(),
                            result.getMessage(),
                            null
                    );
                }
                Media media = result.getData();
                medias.add(media);
            } else if (ownerType == ActivityOwnerType.EVENT) {
                result = mediaService.createMedia(
                        file,
                        null,
                        title,
                        null,
                        activityId,
                        null,
                        null
                );
                if (!"201".equals(result.getCode())) {
                    return new LogicResult<>(
                            result.getCode(),
                            result.getMessage(),
                            null
                    );
                }
                Media media = result.getData();
                medias.add(media);

            } else {
                result = mediaService.createMedia(
                        file,
                        null,
                        title,
                        null,
                        null,
                        null,
                        activityId
                );
                if (!"201".equals(result.getCode())) {
                    return new LogicResult<>(
                            result.getCode(),
                            result.getMessage(),
                            null
                    );
                }
                Media media = result.getData();
                medias.add(media);

            }

        }

        return null;
    }


    /**
     * Handles full media creation workflow for a new activity.
     * This includes:
     * - Video creation
     * - Image uploads
     */
    protected LogicResult<Void> processCreateMedias(
            String videoUrl,
            List<MultipartFile> files,
            List<Media> medias,
            String title,
            Long activityId,
            ActivityOwnerType ownerType
    ) {

        // Handle video
        LogicResult<Void> videoResult =
                handleVideo(videoUrl, medias, title, activityId, ownerType);

        if (videoResult != null) {
            return videoResult;
        }

        // Handle images
        LogicResult<Void> imageResult =
                handleNewImages(files, medias, title, activityId, ownerType);

        if (imageResult != null) {
            return imageResult;
        }

        return null;
    }


    /**
     * Handles full media update workflow.
     * This includes:
     * - Removed medias parsing and deletion
     * - Video create/update/delete
     * - New image uploads
     */
    protected LogicResult<Void> processUpdateMedias(
            String removedMediaIdsJson,
            String videoUrl,
            List<MultipartFile> files,
            List<Media> medias,
            String title,
            Long activityId,
            ActivityOwnerType ownerType
    ) {

        // Parse removed IDs
        List<Long> removedIds = parseRemovedMediaIds(removedMediaIdsJson);

        if (removedIds == null) {
            return new LogicResult<>("400",
                    "Invalid removedMediaIds",
                    null);
        }

        // Delete removed medias
        LogicResult<Void> deleteResult =
                deleteRemovedMedias(removedIds, medias);

        if (deleteResult != null) {
            return deleteResult;
        }

        // Handle video
        LogicResult<Void> videoResult =
                handleVideo(videoUrl, medias, title, activityId, ownerType);

        if (videoResult != null) {
            return videoResult;
        }

        // Handle images
        LogicResult<Void> imageResult =
                handleNewImages(files, medias, title, activityId, ownerType);

        if (imageResult != null) {
            return imageResult;
        }

        return null;
    }


    /**
     * Applies sanitized values to an activity entity.
     * This method centralizes field mapping between
     * sanitized input and domain entity.
     */
    protected void applySanitizedValues(
            ActivityEntity activity,
            SanitizedActivityInput input
    ) {
        activity.setTitle(input.title());
        activity.setDescription(input.description());
        activity.setStartDate(input.startDate());
        activity.setEndDate(input.endDate());
        activity.setLocation(input.location());
        activity.setAddress(input.address());
        activity.setCapacity(input.capacity());
        activity.setVisibility(input.visibility());
    }



    // ----------------------------------- Helper -----------------------------------------
    /**
     * Initial validation error
     */
    protected <T> LogicResult<T> validationError() {
        return new LogicResult<>("400", "Validation error", null);
    }


    /**
     * Validates the title
     */
    protected boolean isValidTitle(String title, LogicResult<?> result) {

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
     * Validates the description
     */
    protected boolean isValidDescription(String description, LogicResult<?> result) {

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
    protected boolean isValidStartDate(LocalDateTime startDate, LogicResult<?> result) {

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
    protected boolean isValidEndDate(LocalDateTime startDate, LocalDateTime endDate, LogicResult<?> result) {

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
     * Validates the location
     */
    protected boolean isValidLocation(String location, LogicResult<?> result) {

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
     * Validates the address
     */
    protected boolean isValidAddress(String address, LogicResult<?> result) {

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
     * Validates the capacity
     */
    protected boolean isValidCapacity(int capacity, LogicResult<?> result) {

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
    protected boolean isValidVisibility(Visibility visibility, LogicResult<?> result) {

        if (visibility == null) {
            result.setMessage("The visibility is required");
            return false;
        }

        return true;
    }


    // ---------------------------- private validate methods -------------------------------
    /**
     * Validates and sanitizes all fields of a ActivityDTO

     * This method centralizes:
     * - XSS sanitization
     * - Business validation rules

     * If a validation rule fails, the provided LogicResult is filled
     * and the method returns null.
     *
     * @param dto Input DTO
     * @param result Result object used to store validation error messages
     *
     * @return A SanitizedActivityInput object if valid, or null if validation fails
     */
    protected SanitizedActivityInput validateAndSanitizeActivity(
            ActivityDTO dto,
            LogicResult<?> result
    ) {

        String title = InputSanitizer.sanitize(dto.getTitle());
        String description = InputSanitizer.sanitize(dto.getDescription());
        String location = InputSanitizer.sanitize(dto.getLocation());
        String address = InputSanitizer.sanitize(dto.getAddress());

        int capacity = dto.getCapacity();
        LocalDateTime startDate = dto.getStartDate();
        LocalDateTime endDate = dto.getEndDate();
        Visibility visibility = dto.getVisibility();

        if (!isValidTitle(title, result)) return null;
        if (!isValidDescription(description, result)) return null;
        if (!isValidStartDate(startDate, result)) return null;
        if (!isValidEndDate(startDate, endDate, result)) return null;
        if (!isValidVisibility(visibility, result)) return null;
        if (!isValidLocation(location, result)) return null;
        if (!isValidAddress(address, result)) return null;
        if (!isValidCapacity(capacity, result)) return null;

        return new SanitizedActivityInput(
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
     * Immutable container for sanitized and validated input data

     * This record is used as an internal data structure between:
     * - the validation layer
     * - and the business logic of create/update operations

     * Responsibilities:
     * - Hold only trusted, sanitized values
     * - Prevent propagation of raw user input further in the service layer
     * - Guarantee that all fields have passed business validation rules

     * This object is never exposed outside the service layer
     */
    protected record SanitizedActivityInput(
            String title,
            String description,
            LocalDateTime startDate,
            LocalDateTime endDate,
            String location,
            String address,
            int capacity,
            Visibility visibility
    ) {}


}
