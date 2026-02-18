import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {NewsService} from '../../../services/news/news-service';
import {environment} from '../../../../environments/environment';
import {EditorComponent} from '@tinymce/tinymce-angular';

@Component({
  selector: 'app-news-edit-component',
  imports: [
    ReactiveFormsModule,
    EditorComponent
  ],
  templateUrl: './news-edit-component.html',
  styleUrl: './news-edit-component.scss',
})
export class NewsEditComponent implements OnInit {
  newsForm!: FormGroup;
  newsId!: number;
  selectedFile?: File;
  errorMessage?: string;
  existingMediaPath?: string;

  constructor(
  private readonly route: ActivatedRoute,
  private readonly fb: FormBuilder,
  private readonly newsService: NewsService,
  private readonly router: Router
  ) {}

  private handleError(err: any, fallbackMessage: string): void {
    this.errorMessage =
      err?.error?.message ?? fallbackMessage;
  }

  ngOnInit(): void {
    this.newsId = Number(this.route.snapshot.paramMap.get('id'));
    this.initForm();
    this.loadNews();
  }

  private initForm(): void {
    this.newsForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(150)]],
      content: ['', Validators.required],
      visibility: ['PUBLIC', Validators.required],
      contentStatus: ['CREATED', Validators.required],
      publishedAt: [null]
    });
  }

  private loadNews(): void {
    this.newsService.getNewsById(this.newsId).subscribe({
      next: result => {
        if (result.code === '200' && result.data) {
          this.newsForm.patchValue({
            title: result.data.title,
            content: result.data.content,
            visibility: result.data.visibility,
            contentStatus: result.data.contentStatus,
            publishedAt: result.data.publishedAt
          });
          // Retrieve existing medias
          if (result.data.mediaList?.length) {
            this.existingMediaPath = result.data.mediaList[0].imagePath;
          }
        }
      },
      error: err => console.error('Error loading news', err)
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  submit(): void {
    if (this.newsForm.invalid) {
      this.newsForm.markAllAsTouched();
      return;
    }

    const formData = new FormData();

    const newsDTO = {
      title: this.newsForm.value.title,
      content: this.newsForm.value.content,
      visibility: this.newsForm.value.visibility,
      contentStatus: this.newsForm.value.contentStatus,
      publishedAt: this.newsForm.value.publishedAt
    };

    formData.append(
      'news',
      new Blob([JSON.stringify(newsDTO)], { type: 'application/json' })
    );

    if (this.selectedFile) {
      formData.append('files', this.selectedFile);
    }

    this.newsService.updateNews(this.newsId, formData).subscribe({
      next: () => {
        this.router.navigate(['/admin/news/management']).then(r => {})
      },
      error: err => this.handleError(err, 'Error updating news')
    });
  }

  protected readonly environment = environment;


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
