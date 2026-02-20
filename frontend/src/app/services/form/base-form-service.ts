import { Injectable } from '@angular/core';
import {FormGroup} from '@angular/forms';

@Injectable({
  providedIn: 'root',
})
export class BaseFormService {
  /**
   * Validate form and return error message if invalid
   * @param form
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
   * @param form
   * @param entityKey
   */
  buildFormData(form: FormGroup, entityKey: string): FormData {

    const formData = new FormData();

    const dto = {
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
      entityKey,
      new Blob(
        [JSON.stringify(dto)],
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
