/**
 * Generic pagination model matching Spring Page<T>
 */
export interface Page<T> {
  content: T[]; // Page data
  number: number; // Current page index (0-based)
  size: number; // Page size
  totalPages: number; // Total number of pages
  totalElements: number; // Total number of elements
  first: boolean; // Is first page
  last: boolean; // Is last page
}
