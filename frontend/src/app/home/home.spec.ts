import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Home } from './home';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('Home', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        Home
      ],
      providers: [
        provideHttpClient(), // Provides HttpClient
        provideHttpClientTesting(), // Mocks HTTP backend
        provideRouter([])  // Mocks Router
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
