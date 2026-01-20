import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Header } from './header';
import {provideRouter} from '@angular/router';

describe('Header', () => {
  // Variable that will store the Header component instance
  let component: Header;

  // Variable that will store the component fixture
  let fixture: ComponentFixture<Header>;

  // This function runs before each test case
  beforeEach(async () => {

    // Configure the testing module
    await TestBed.configureTestingModule({

      // Import the standalone Header component
      imports: [Header],

      // Provide required routing dependencies
      providers: [
        provideRouter([]) // Provides Router and ActivatedRoute services
      ]

      // Compile component template and styles
    }).compileComponents();

    // Create an instance of the Header component
    fixture = TestBed.createComponent(Header);

    // Retrieve the component instance from the fixture
    component = fixture.componentInstance;

    // Trigger initial Angular change detection
    fixture.detectChanges();
  });

  // Define a test to check if the component is created
  it('should create', () => {

    // Expect the component to be successfully instantiated
    expect(component).toBeTruthy();
  });
});
