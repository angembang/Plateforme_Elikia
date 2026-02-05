import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsDetailComponent } from './news-detail-component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('NewsDetailComponent', () => {
  let component: NewsDetailComponent;
  let fixture: ComponentFixture<NewsDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NewsDetailComponent
      ],
      providers: [
      provideHttpClient(), // Provides HttpClient
      provideHttpClientTesting(), // Mocks HTTP backend
      provideRouter([])  // Mocks Router
    ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewsDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
