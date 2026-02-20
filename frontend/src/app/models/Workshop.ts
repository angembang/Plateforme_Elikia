import {Media} from './Media';

/**
 * Represent a workshop entity received from the backend
 *
 */
export interface Workshop {
  // The unique identifier of the workshop
  workshopId: number;

  // The title of the workshop
  title: string;

  // The description of the workshop
  description: string;

  // Start date
  startDate: string;

  // End date
  endDate: string;

  // The location of the workshop
  location: string;

  // The address of the workshop
  address: string;

  // The capacity of the workshop
  capacity: number;

  // Visibility status (PUBLIC / PRIVATE)
  visibility: 'PUBLIC' | 'MEMBER_ONLY';

  // Associated medias
  mediaList?: Media[];

  displayType?: 'IMAGE' | 'VIDEO' | 'NONE';
}
