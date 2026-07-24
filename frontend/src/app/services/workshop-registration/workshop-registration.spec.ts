import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { WorkshopRegistrationService } from './workshop-registration';

describe('WorkshopRegistrationService', () => {
  let service: WorkshopRegistrationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(WorkshopRegistrationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
