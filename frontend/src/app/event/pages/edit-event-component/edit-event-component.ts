import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {EventService} from '../../../services/event/event-service';
import {environment} from '../../../../environments/environment';
import {EditorComponent} from '@tinymce/tinymce-angular';
import {SafeUrlPipe} from '../../../safe-url-pipe';
import {FilePreviewPipe} from '../../../file-preview-pipe';
import {ErrorHandlerService} from '../../../services/error/error-handler-service';
import {EventFormService} from '../../../services/event/event-form-service';

@Component({
  selector: 'app-edit-event-component',
  imports: [
    EditorComponent,
    FormsModule,
    ReactiveFormsModule,
    SafeUrlPipe,
    FilePreviewPipe,
  ],
  templateUrl: './edit-event-component.html',
  styleUrl: './edit-event-component.scss',
})
export class EditEventComponent implements OnInit {
  eventForm!: FormGroup;
  eventId!: number;
  errorMessage?: string;

  // Existing medias
  existingImages: { id: number; path: string }[] = [];
  existingVideoUrl?: { id: number; url: string};

// New medias
  selectedImages: File[] = [];
  selectedVideoUrl?: string;

// Removed images
  removedMediaIds: number[] = [];

  protected readonly environment = environment;

  constructor(
    private readonly route: ActivatedRoute,
    private readonly fb: FormBuilder,
    private readonly eventService: EventService,
    private readonly router: Router,
    private readonly errorHandler: ErrorHandlerService,
    private readonly eventFormService: EventFormService
  ) {}


  ngOnInit(): void {
    this.eventId = Number(this.route.snapshot.paramMap.get('id'));
    this.initForm();
    this.loadEvent();
  }

  private initForm() {
    this.eventForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(255)]],
      description: ['', [Validators.required, Validators.maxLength(2000)]],
      startDate: ['', [Validators.required]],
      endDate: ['', [Validators.required]],
      location: ['', [Validators.required, Validators.maxLength(255)]],
      address: ['', [Validators.required, Validators.maxLength(255)]],
      capacity: [0, [Validators.required, Validators.max(50000)]],
      visibility: ['PUBLIC', Validators.required],
      videoUrl: ['', [Validators.pattern(/^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.be)\/.+$/)]]
    });

  }


  private loadEvent() {
    this.eventService.getEventById(this.eventId).subscribe({
      next: result => {
        if (result.code && result.data) {
          this.eventForm.patchValue({
            title: result.data.title,
            description: result.data.description,
            startDate: result.data.startDate,
            endDate: result.data.endDate,
            location: result.data.location,
            address: result.data.address,
            capacity: result.data.capacity,
            visibility: result.data.visibility
          });
          // Retrieve existing medias
          if(result.data.mediaList?.length) {
            this.existingImages = result.data.mediaList
              .filter(m => m.imagePath)
              .map(m => ({
                id: m.mediaId,
                path: m.imagePath!
              }));

            const video = result.data.mediaList.find(m => m.videoUrl);

            if (video && video.videoUrl) {

              this.existingVideoUrl = {
                id: video.mediaId,
                url: video.videoUrl
              };

              // Pre-fill the form field
              this.eventForm.patchValue({
                videoUrl: video.videoUrl
              });
            }
          }
        }
      },
      error: err => console.error('Error loading news', err)
    })
  }


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

    // Images
    this.selectedImages.forEach(file => {
      formData.append('files', file);
    });

    // Removed medias
    if (this.removedMediaIds.length) {
      formData.append(
        'removedMediaIds',
        new Blob(
          [JSON.stringify(this.removedMediaIds)],
          { type: 'application/json' }
        )
      );
    }

    this.eventService.updateEvent(this.eventId, formData)
      .subscribe({
        next: result => {
          if (result.code !== '200') {
            this.errorMessage = result.message;
            return;
          }

          this.router.navigate(['/admin/event']).then(r => {});
        },
        error: err => {
          this.errorMessage =
            this.errorHandler.handleError(err, 'Server error');
        }
      });
  }


  /**
   * Mange multi images
   * @param event
   */
  onImagesSelected(event: Event): void {
    // Reset before adding
    this.selectedImages = [];

    const input = event.target as HTMLInputElement;

    if (!input.files) return;

    for (const file of Array.from(input.files)) {

      if (file.size > 10 * 1024 * 1024) {
        this.errorMessage = 'Image trop lourde (max 10MB)';
        continue;
      }

      this.selectedImages.push(file);
    }
  }


  /**
   * Remove existing image
   * @param img
   */
  removeExistingImage(img: { id: number; path: string }) {

    this.existingImages =
      this.existingImages.filter(i => i.id !== img.id);

    if(!this.removedMediaIds.includes(img.id)) {
      this.removedMediaIds.push(img.id)
    }
  }


  /**
   * convert the url before display the video
   * @param url
   */
   getEmbedUrl(url: string): string {

    if (!url) return '';

    const match = url.match(
      /(?:youtube\.com\/watch\?v=|youtu\.be\/)([^&]+)/
    );

    return match
      ? `https://www.youtube.com/embed/${match[1]}`
      : url;
  }


  /**
   * remove existing video
   * @protected
   */
  protected removeExistingVideo() {
    if (!this.existingVideoUrl) return;

    // Add to removedMediaIds
    if (!this.removedMediaIds.includes(this.existingVideoUrl.id)) {
      this.removedMediaIds.push(this.existingVideoUrl.id);
    }

    // Delete side ui
    this.existingVideoUrl = undefined;
    this.eventForm.patchValue({videoUrl: ''});

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
