import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {EditorComponent} from '@tinymce/tinymce-angular';
import {EventService} from '../../../services/event/event-service';
import {Router} from '@angular/router';
import {environment} from '../../../../environments/environment';
import {ErrorHandlerService} from '../../../services/error/error-handler-service';
import {EventFormService} from '../../../services/event/event-form-service';

@Component({
  selector: 'app-create-event-component',
  imports: [
    ReactiveFormsModule,
    EditorComponent
  ],
  templateUrl: './create-event-component.html',
  styleUrl: './create-event-component.scss',
})
export class CreateEventComponent implements OnInit {
  // Reactive form instance
  eventForm!: FormGroup;
  // error message
  errorMessage?: string;

  // Optional image file
  selectedFiles: File[] = [];

  private handleError(err: any, fallbackMessage: string): void {
    console.error('HTTP Error:', err);
    // Backend sends LogicResult
    if (err?.error?.message) {
      this.errorMessage = err.error.message;
      return;
    }
    // Backend sends string
    if (typeof err?.error === 'string') {
      this.errorMessage = err.error;
      return;
    }
    // Angular error message
    if (err?.message) {
      this.errorMessage = err.message;
      return;
    }
    // Fallback
    this.errorMessage = fallbackMessage;
  }


  // Constructor
  constructor(
    private readonly fb: FormBuilder,
    private readonly eventService: EventService,
    private readonly router: Router,
    private readonly errorHandler: ErrorHandlerService,
    private readonly eventFormService: EventFormService) {}

  ngOnInit(): void {
    this.initForm();
  }


  /**
   * Initialize the reactive form
   */
  private initForm(): void {
    this.eventForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(150)]],
      description: ['', [Validators.required, Validators.maxLength(2000)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      location: ['', [Validators.required, Validators.maxLength(30)]],
      address: ['', [Validators.required, Validators.maxLength(200)]],
      capacity: [0, [Validators.required, Validators.min(1)]],
      visibility: ['PUBLIC', Validators.required],
      videoUrl: ['', [Validators.pattern(/^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.be)\/.+$/)]]
    });
  }


  // Handle file selection from input[type=file]
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files) return;

    for (const element of input.files) {
      const file = element;

      if (file.size > 10 * 1024 * 1024) {
        this.errorMessage = "Image trop lourde (max 10MB)";
        continue;
      }

      this.selectedFiles.push(file);
    }
  }


  /**
   * Submit form to backend
   */
  submit(): void {
    // Clear the error message
    this.errorMessage = undefined;
    const validationError =
      this.eventFormService.validateForm(this.eventForm);

    if (validationError) {
      this.eventForm.markAllAsTouched();
      this.errorMessage = validationError;
      return;
    }

    const formData =
      this.eventFormService.buildFormData(this.eventForm);

    // Files
    this.selectedFiles.forEach(file => {
      formData.append('files', file);
    });

    const hasImage = this.selectedFiles.length > 0;
    const hasVideo = !!this.eventForm.value.videoUrl;

    if (!hasImage && !hasVideo) {
      this.errorMessage =
        'Veuillez fournir une image ou une vidéo';
      return;
    }

    this.eventService.createEvent(formData)
      .subscribe({
        next: () => {
          this.router.navigate(['/admin/event/']).then(r => {});
        },
        error: err => {
          this.errorMessage =
            this.errorHandler.handleError(err, 'Error creating event');
        }
      });
  }


  /**
   * videoUrl validation
   * @param url
   * @private
   */
  private isValidYoutubeUrl(url: string): boolean {
    const regex =
      /^(https:\/\/)(www\.)?(youtube\.com|youtu\.be)\/.+$/;

    return regex.test(url);
  }


  /**
   * Configuration tinymce
   */
  editorConfig = {
    height: 350,
    menubar: false,
    plugins: [
      'lists',
      'link',
      'image',
      'preview',
      'code',
      'wordcount'
    ],
    toolbar:
      'undo redo | bold italic underline | ' +
      'bullist numlist | alignleft aligncenter alignright | ' +
      'link | removeformat',
    branding: false
  };

  // Tinymce api key
  tinymceApikey = environment.tinymceApiKey


}
