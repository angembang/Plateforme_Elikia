import { Injectable } from '@angular/core';
import {FormGroup} from '@angular/forms';

@Injectable({
  providedIn: 'root',
})
export class EventFormService {
  /**
   * Validate form and return error message if invalid
   */
  validateForm(form: FormGroup): string | null {

    if (!form.invalid) return null;

    if (form.get('title')?.invalid) {
      return 'Le titre est obligatoire et doit contenir entre 2 et 255 caractères';
    }

    if (form.get('description')?.invalid) {
      return 'La description doit contenir entre 2 et 2000 caractères';
    }

    if (form.get('videoUrl')?.invalid) {
      return 'URL YouTube invalide';
    }

    if (form.get('location')?.invalid) {
      return 'Le lieu doit contenir entre 3 et 255 caractères';
    }

    if (form.get('address')?.invalid) {
      return 'L’adresse doit contenir entre 5 et 255 caractères';
    }

    if (form.get('capacity')?.invalid) {
      return 'Capacité invalide';
    }

    return 'Formulaire invalide';
  }


  /**
   * Build FormData from form
   */
  buildFormData(form: FormGroup): FormData {

    const formData = new FormData();

    const eventDTO = {
      title: form.value.title,
      description: form.value.description,
      startDate: form.value.startDate,
      endDate: form.value.endDate,
      location: form.value.location,
      address: form.value.address,
      capacity: form.value.capacity,
      visibility: form.value.visibility,
    };

    formData.append(
      'event',
      new Blob(
        [JSON.stringify(eventDTO)],
        { type: 'application/json' }
      )
    );

    const video = form.value.videoUrl?.trim();

    if (video) {
      formData.append('videoUrl', video);
    }

    return formData;
  }

}
