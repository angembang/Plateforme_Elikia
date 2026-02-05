import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NewsCreateComponent } from './news-create-component';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';
import {provideRouter} from '@angular/router';

describe('NewsCreateComponent', () => {
  let component: NewsCreateComponent;
  let fixture: ComponentFixture<NewsCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        NewsCreateComponent
      ],
      providers: [
        provideHttpClient(), // Provides HttpClient
        provideHttpClientTesting(), // Mocks HTTP backend
        provideRouter([])  // Mocks Router
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NewsCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
