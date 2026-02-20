import { TestBed } from '@angular/core/testing';

import { WorkshopService } from './workshop-service';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';

describe('WorkshopService', () => {
  let service: WorkshopService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(), // Provides HttpClient
        provideHttpClientTesting() // Mocks HTTP backend
      ]
    });
    service = TestBed.inject(WorkshopService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
