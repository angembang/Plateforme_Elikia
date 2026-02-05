import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsEditComponent } from './news-edit-component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('NewsEditComponent', () => {
  let component: NewsEditComponent;
  let fixture: ComponentFixture<NewsEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NewsEditComponent
      ],
      providers: [
        provideHttpClient(), // Provides HttpClient
        provideHttpClientTesting(), // Mocks HTTP backend
        provideRouter([])  // Mocks Router
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewsEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
