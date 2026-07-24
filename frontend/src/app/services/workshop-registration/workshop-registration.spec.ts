import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { WorkshopRegistrationService } from './workshop-registration';

describe('WorkshopRegistrationService', () => {
  let service: WorkshopRegistrationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });

    service = TestBed.inject(WorkshopRegistrationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
