import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { EventRegistrationService } from './event-registration';

describe('EventRegistrationService', () => {
  let service: EventRegistrationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    service = TestBed.inject(EventRegistrationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
