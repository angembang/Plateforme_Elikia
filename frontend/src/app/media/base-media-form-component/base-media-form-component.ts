import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {environment} from '../../../environments/environment';

export class BaseMediaFormComponent {
  // Reactive form instance
  form!: FormGroup;
  // error message
  errorMessage?: string;
  // Optional image file
  selectedFiles: File[] = [];

  // Existing medias
  existingImages: { id: number; path: string }[] = [];
  existingVideoUrl?: { id: number; url: string};

  // Removed images
  removedMediaIds: number[] = [];

  // Constructor
  protected constructor(protected readonly fb: FormBuilder) {}

  /**
   * Initialize the reactive form
   */
  protected initBaseForm(): FormGroup {
    return this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(150)]],
      description: ['', [Validators.required, Validators.maxLength(2000)]],
      startDate: ['', Validators.required],
      endDate: ['', Validators.required],
      location: ['', [Validators.required, Validators.maxLength(30)]],
      address: ['', [Validators.required, Validators.maxLength(200)]],
      capacity: [0, [Validators.required, Validators.min(1)]],
      visibility: ['PUBLIC', Validators.required],
      videoUrl: ['', [
        Validators.pattern(/^(https?:\/\/)?(www\.)?(youtube\.com|youtu\.be)\/.+$/)
      ]]
    });
  }

  // Handle file selection from input[type=file]
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files) return;

    for (const file of Array.from(input.files)) {

      if (file.size > 10 * 1024 * 1024) {
        this.errorMessage = 'Image trop lourde (max 10MB)';
        continue;
      }

      this.selectedFiles.push(file);
    }
  }


  /**
   * Manage files
   * @param formData
   * @protected
   */
  protected appendMediaAndValidate(formData: FormData): boolean {

    // Append files
    this.selectedFiles.forEach(file => {
      formData.append('files', file);
    });

    const hasImage = this.selectedFiles.length > 0;
    const hasVideo = !!this.form.value.videoUrl;

    if (!hasImage && !hasVideo) {
      this.errorMessage = 'Veuillez fournir une image ou une vidéo';
      return false;
    }

    return true;
  }


  /**
   *
   * @param data
   * @protected
   */
  protected patchFormAndMedia(data: any): void {

    this.form.patchValue({
      title: data.title,
      description: data.description,
      startDate: data.startDate,
      endDate: data.endDate,
      location: data.location,
      address: data.address,
      capacity: data.capacity,
      visibility: data.visibility
    });

    if (!data.mediaList?.length) return;

    this.existingImages = data.mediaList
      .filter((m: any) => m.imagePath)
      .map((m: any) => ({
        id: m.mediaId,
        path: m.imagePath
      }));

    const video = data.mediaList.find((m: any) => m.videoUrl);

    if (video?.videoUrl) {
      this.existingVideoUrl = {
        id: video.mediaId,
        url: video.videoUrl
      };

      this.form.patchValue({
        videoUrl: video.videoUrl
      });
    }
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
