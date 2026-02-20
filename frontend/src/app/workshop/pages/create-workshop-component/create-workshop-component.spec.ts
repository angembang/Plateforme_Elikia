import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateWorkshopComponent } from './create-workshop-component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('CreateWorkshopComponent', () => {
  let component: CreateWorkshopComponent;
  let fixture: ComponentFixture<CreateWorkshopComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        CreateWorkshopComponent
      ],
      providers: [
        provideHttpClient(), // Provides HttpClient
        provideHttpClientTesting(), // Mocks HTTP backend
        provideRouter([])  // Mocks Router
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateWorkshopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
