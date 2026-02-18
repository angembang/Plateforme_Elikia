import {Media} from './Media';

/**
 * Represent an event entity received from the backend
 *
 */
export interface EventElikia {
  // The unique identifier of the event
  eventId: number;

  // The title of the event
  title: string;

  // The description of the event
  description: string;

  // Start date
  startDate: string;

  // End date
  endDate: string;

  // The location of the event
  location: string;

  // The address of the event
  address: string;

  // The capacity of the event
  capacity: number;

  // Visibility status (PUBLIC / PRIVATE)
  visibility: 'PUBLIC' | 'MEMBER_ONLY';

  // Associated medias
  mediaList?: Media[];

  displayType?: 'IMAGE' | 'VIDEO' | 'NONE';
}
