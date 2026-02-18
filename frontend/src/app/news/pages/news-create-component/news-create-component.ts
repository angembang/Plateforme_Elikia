import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NewsService} from '../../../services/news/news-service';
import {Router} from '@angular/router';
import {EditorComponent} from '@tinymce/tinymce-angular';
import {environment} from '../../../../environments/environment';

@Component({
  selector: 'app-news-create-component',
  imports: [
    ReactiveFormsModule,
    EditorComponent
  ],
  templateUrl: './news-create-component.html',
  styleUrl: './news-create-component.scss',
})
export class NewsCreateComponent implements OnInit {
  // Reactive form instance
  newsForm!: FormGroup;
  // error message
  errorMessage?: string;

  // Optional image file
  selectedFile?: File;

  private handleError(err: any, fallbackMessage: string): void {
    this.errorMessage =
      err?.error?.message ?? fallbackMessage;
  }

  // Constructor
  constructor(
    private readonly fb: FormBuilder,
    private readonly newsService: NewsService,
    private readonly router: Router) {}

  ngOnInit(): void {
    this.initForm();
  }


  /**
   * Initialize the reactive form
   */
  private initForm(): void {
    this.newsForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(150)]],
      content: ['', Validators.required],
      visibility: ['PUBLIC', Validators.required],
      contentStatus: ['CREATED', Validators.required],
      publishedAt: [null]
    });
  }


  /**
   * Handle file selection from input[type=file]
   */
  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      const file = input.files[0];
      if (file.size > 10 * 1024 * 1024) {
        this.errorMessage = "Image trop lourde (max 10MB)";
        return;
      }
      this.selectedFile = file;
    }
  }


  /**
   * Submit form to backend
   */
  submit(): void {
    if (this.newsForm.invalid) {
      this.newsForm.markAllAsTouched();
      return;
    }

    // Create FormData object (required by backend)
    const formData = new FormData();
    // Build NewsDTO object
    const newsDTO = {
      title: this.newsForm.value.title,
      content: this.newsForm.value.content,
      visibility: this.newsForm.value.visibility,
      contentStatus: this.newsForm.value.contentStatus,
      publishedAt: this.newsForm.value.publishedAt
    };

    // Append JSON as Blob
    formData.append(
      'news',
      new Blob([JSON.stringify(newsDTO)], {type: 'application/json'})
    );

    // Append file(s) if present
    if (this.selectedFile) {
      formData.append('files', this.selectedFile);
    }

    this.newsService.createNews(formData).subscribe({
      next: () =>  {
        this.router.navigate(['/admin/news/management']).then(r => {});
      },
      error: err => this.handleError(err, 'Error creating news')
    });
  }


  create(): void {
    this.newsForm.patchValue({
      contentStatus: 'CREATED'
    });
    if (!this.newsForm.value.publishedAt) {
      this.errorMessage = 'Veuillez renseigner une date de publication';
      return;
    }
    this.submit();
  }


  publish(): void {
    this.newsForm.patchValue({
      contentStatus: 'PUBLISHED',
      publishedAt: null
    });
    this.submit();
  }


  /**
   * Deactivate create and published button according to the selected content status
   */
  get isCreated(): boolean {
    return this.newsForm.value.contentStatus === 'CREATED';
  }

  get isPublished(): boolean {
    return this.newsForm.value.contentStatus === 'PUBLISHED';
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
