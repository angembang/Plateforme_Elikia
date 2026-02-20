import {Component, OnInit} from '@angular/core';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {WorkshopService} from '../../../services/workshop/workshop-service';
import {Router} from '@angular/router';
import {ErrorHandlerService} from '../../../services/error/error-handler-service';
import {EditorComponent} from '@tinymce/tinymce-angular';
import {BaseFormService} from '../../../services/form/base-form-service';
import {BaseMediaFormComponent} from '../../../media/base-media-form-component/base-media-form-component';

@Component({
  selector: 'app-create-workshop-component',
  imports: [
    EditorComponent,
    ReactiveFormsModule
  ],
  templateUrl: './create-workshop-component.html',
  styleUrl: './create-workshop-component.scss',
})
export class CreateWorkshopComponent extends BaseMediaFormComponent implements OnInit {
  // Constructor
  constructor(
    fb: FormBuilder,
    private readonly workshopService: WorkshopService,
    private readonly router: Router,
    private readonly errorHandler: ErrorHandlerService,
    private readonly baseFormService: BaseFormService) {
    super(fb);
  }

  ngOnInit(): void {
    this.form = this.initBaseForm();
  }


  /**
   * Submit form to backend
   */
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

    // Files
    if (!this.appendMediaAndValidate(formData)) {
      return;
    }

    this.workshopService.createWorkshop(formData)
      .subscribe({
        next: () => {
          this.router.navigate(['/admin/workshop/']).then(r => {});
        },
        error: err => {
          this.errorMessage =
            this.errorHandler.handleError(err, 'Error creating workshop');
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

}
