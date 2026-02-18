import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ErrorHandlerService {
  handleError(err: any, fallback: string): string {

    console.error('HTTP Error:', err);

    if (err?.error?.message) {
      return err.error.message;
    }

    if (typeof err?.error === 'string') {
      return err.error;
    }

    if (err?.message) {
      return err.message;
    }

    return fallback;
  }

}
