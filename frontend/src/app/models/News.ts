import {Media} from './Media';

/**
 * Represent a news entity received from the backend
 *
 */
export interface News {
  // The unique identifier of the news
  newsId: number;

  // The title of the news
  title: string;

  // The content of the news
  content: string;

  // Publication date
  publishedAt: string;

  // Visibility status (PUBLIC / PRIVATE)
  visibility: 'PUBLIC' | 'MEMBER_ONLY';

  // Content status (CREATED / PUBLISHED)
  contentStatus: 'CREATED' | 'PUBLISHED';

  // Associated medias
  mediaList?: Media[];
}
