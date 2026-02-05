/**
 * Represents a Media entity returned by the backend
 *
 * A Media can belong to exactly ONE parent:
 * - News
 * - Event
 * - Workshop
 * - Achievement
 */
export interface Media {

  // Unique identifier of the media
  mediaId: number;

  // Optional caption describing the media
  caption?: string;

  // Image file path (stored on server)
  imagePath?: string;

  // YouTube video URL
  videoUrl?: string;

  // Parent identifiers (only one is non-null)
  newsId?: number;
  eventId?: number;
  workshopId?: number;
  achievementId?: number;
}
