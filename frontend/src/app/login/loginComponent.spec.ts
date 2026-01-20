import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginComponent } from './loginComponent';
import {provideRouter} from '@angular/router';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideHttpClient} from '@angular/common/http';

describe('LoginComponent', () => {
  let component: LoginComponent; // Holds the component instance
  let fixture: ComponentFixture<LoginComponent>; // Holds the testing fixture

  beforeEach(async () => {
    // Configure the testing module for this test suite
    await TestBed.configureTestingModule({
      imports: [
        LoginComponent // Import the standalone component
      ],
      providers: [
        provideRouter([]), // Provide Router and ActivatedRoute
        provideHttpClient(), // Provide HttpClient service
        provideHttpClientTesting() // Provide HttpTestingController backend
      ]
    }).compileComponents(); // Compile templates and styles

    // Create the component instance
    fixture = TestBed.createComponent(LoginComponent);

    // Access the component class instance
    component = fixture.componentInstance;

    // Run initial change detection
    fixture.detectChanges();
  });

  // Test case: verify that the component is created successfully
  it('should create', () => {
    expect(component).toBeTruthy(); // Component should exist
  });
});
