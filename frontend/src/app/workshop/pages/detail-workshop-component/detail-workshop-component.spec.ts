import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailWorkshopComponent } from './detail-workshop-component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('DetailWorkshopComponent', () => {
  let component: DetailWorkshopComponent;
  let fixture: ComponentFixture<DetailWorkshopComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        DetailWorkshopComponent
      ],
      providers: [
        provideHttpClient(), // Provides HttpClient
        provideHttpClientTesting(), // Mocks HTTP backend
        provideRouter([])  // Mocks Router
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetailWorkshopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
