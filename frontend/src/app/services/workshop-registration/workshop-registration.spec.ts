import { TestBed } from '@angular/core/testing';

import { WorkshopRegistration } from './workshop-registration';

describe('WorkshopRegistration', () => {
  let service: WorkshopRegistration;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorkshopRegistration);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
