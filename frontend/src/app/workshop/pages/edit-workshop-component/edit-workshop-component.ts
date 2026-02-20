import {Component, OnInit} from '@angular/core';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';;
import {ErrorHandlerService} from '../../../services/error/error-handler-service';
import { environment } from "../../../../environments/environment";
import {WorkshopService} from '../../../services/workshop/workshop-service';
import {EditorComponent} from '@tinymce/tinymce-angular';
import {FilePreviewPipe} from '../../../pipe/preview/file-preview-pipe';
import {SafeUrlPipe} from '../../../pipe/url/safe-url-pipe';
import {BaseFormService} from '../../../services/form/base-form-service';
import {BaseMediaFormComponent} from '../../../media/base-media-form-component/base-media-form-component';

@Component({
  selector: 'app-edit-workshop-component',
  imports: [
    EditorComponent,
    FilePreviewPipe,
    ReactiveFormsModule,
    SafeUrlPipe
  ],
  templateUrl: './edit-workshop-component.html',
  styleUrl: './edit-workshop-component.scss',
})
export class EditWorkshopComponent extends BaseMediaFormComponent implements OnInit {
  workshopId!: number;

  protected readonly environment = environment;

  constructor(
    private readonly route: ActivatedRoute,
    fb: FormBuilder,
    private readonly workshopService: WorkshopService,
    private readonly router: Router,
    private readonly errorHandler: ErrorHandlerService,
    private readonly baseFormService: BaseFormService) {
    super(fb);
  }


  ngOnInit(): void {
    this.workshopId = Number(this.route.snapshot.paramMap.get('id'));
    this.form = this.initBaseForm();
    this.loadWorkshop();
  }


  private loadWorkshop() {
    this.workshopService.getWorkshopById(this.workshopId).subscribe({
      next: result => {
        if (result.code && result.data) {
          this.patchFormAndMedia(result.data)
        }
      },
      error: err => console.error('Error loading workshop', err)
    })
  }


  submit(): void {
    // Clear the error message
    this.errorMessage = undefined;
    const validationError =
      this.baseFormService.validateForm(this.form);

    if (validationError) {
      this.form.markAllAsTouched();
      this.errorMessage = validationError;
      return;
    }

    const formData =
      this.baseFormService.buildFormData(this.form, 'workshop');

    // Images
    this.selectedFiles.forEach(file => {
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

    this.workshopService.updateWorkshop(this.workshopId, formData)
      .subscribe({
        next: result => {
          if (result.code !== '200') {
            this.errorMessage = result.message;
            return;
          }

          this.router.navigate(['/admin/workshop']).then(r => {});
        },
        error: err => {
          this.errorMessage =
            this.errorHandler.handleError(err, 'Server error');
        }
      });
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

    const regex = /(?:youtube\.com\/watch\?v=|youtu\.be\/)([^&]+)/;
    const match = regex.exec(url);

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
    this.form.patchValue({videoUrl: ''});

  }

}
