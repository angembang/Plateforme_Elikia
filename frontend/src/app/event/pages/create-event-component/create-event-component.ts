import {Component, OnInit} from '@angular/core';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {EditorComponent} from '@tinymce/tinymce-angular';
import {EventService} from '../../../services/event/event-service';
import {Router} from '@angular/router';
import {ErrorHandlerService} from '../../../services/error/error-handler-service';
import {BaseFormService} from '../../../services/form/base-form-service';
import {BaseMediaFormComponent} from '../../../media/base-media-form-component/base-media-form-component';

@Component({
  selector: 'app-create-event-component',
  imports: [
    ReactiveFormsModule,
    EditorComponent
  ],
  templateUrl: './create-event-component.html',
  styleUrl: './create-event-component.scss',
})
export class CreateEventComponent extends BaseMediaFormComponent implements OnInit {
  // Constructor
  constructor(
    fb: FormBuilder,
    private readonly eventService: EventService,
    private readonly router: Router,
    private readonly errorHandler: ErrorHandlerService,
    private readonly baseFormService: BaseFormService) {
    super(fb);
  }

  ngOnInit(): void {
    this.initForm();
  }


  /**
   * Initialize the reactive form
   */
  private initForm(): void {
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
      this.baseFormService.buildFormData(this.form, 'event');

    // Files
    if (!this.appendMediaAndValidate(formData)) {
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

}
