import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth.service';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';
import {AuthStorageService} from './auth-storage.service';
import {LoginRequest} from '../models/LoginRequest';
import {LogicResult} from '../models/LogicResult';
import {environment} from '../../environments/environment';
import {provideHttpClient} from '@angular/common/http';

describe('AuthService', () => {
  // Variable that will hold the AuthService instance
  let service: AuthService;

  // Variable used to intercept and control HTTP requests
  let httpMock: HttpTestingController;

  // Spy object used to mock AuthStorageService methods
  let storageSpy: jasmine.SpyObj<AuthStorageService>;

  // This function runs before each individual test
  beforeEach(() => {

    // Create a spy object for AuthStorageService with the methods we want to observe
    storageSpy = jasmine.createSpyObj('AuthStorageService', [
      'setToken', // Method used to store JWT token
      'hasToken', // Method used to check authentication state
      'clear'     // Method used to clear stored data
    ]);

    // Configure the Angular testing module
    TestBed.configureTestingModule({

      // Provide all dependencies required by AuthService
      providers: [
        // Provide the AuthService to be tested
        AuthService,

        // Replace the real AuthStorageService with our spy object
        { provide: AuthStorageService, useValue: storageSpy },

        // Provide the real HttpClient service
        provideHttpClient(),

        // Provide a mocked HTTP backend to intercept HTTP calls
        provideHttpClientTesting()
      ]
    });

    // Inject the AuthService instance from the testing module
    service = TestBed.inject(AuthService);

    // Inject the HttpTestingController to control HTTP requests
    httpMock = TestBed.inject(HttpTestingController);
  });

  // This function runs after each test
  afterEach(() => {

    // Verify that no unexpected HTTP requests are still pending
    httpMock.verify();
  });


  /**
   * Test for Service creation
   */
  it('should be created', () => {

    // Expect the service to be instantiated successfully
    expect(service).toBeTruthy();
  });


  /**
   * Test for Successful login and token storage
   */
  it('should login successfully and store the JWT token', () => {

    // GIVEN: a fake login request payload
    const payload: LoginRequest = {
      email: 'admin@mail.com',      // User email
      password: 'password123'      // User password
    };

    // GIVEN: a fake successful backend response
    const mockResponse: LogicResult<string> = {
      code: '200',                 // Business success code
      message: 'Login successful',// Success message
      data: 'fake-jwt-token'      // Fake JWT token returned by backend
    };

    // WHEN: call the login() method
    service.login(payload).subscribe(result => {

      // THEN: the response code should be "200"
      expect(result.code).toBe('200');

      // AND: the returned token should match the fake token
      expect(result.data).toBe('fake-jwt-token');

      // AND: the token should be stored using AuthStorageService
      expect(storageSpy.setToken).toHaveBeenCalledWith('fake-jwt-token');
    });

    // Expect exactly one HTTP POST request to the login endpoint
    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);

    // Verify that the HTTP method is POST
    expect(req.request.method).toBe('POST');

    // Send the fake response to resolve the HTTP call
    req.flush(mockResponse);
  });


  /**
   * Test for login failure (401 response)
   */
  it('should return an error LogicResult when login fails', () => {

    // GIVEN: wrong login credentials
    const payload: LoginRequest = {
      email: 'admin@mail.com',     // Valid email
      password: 'wrong-password'  // Invalid password
    };

    // GIVEN: a fake error response from backend
    const mockError: LogicResult<string> = {
      code: '401',                // Business error code
      message: 'Invalid credentials', // Error message
      data: null                 // No token returned
    };

    // WHEN: call the login() method
    service.login(payload).subscribe(result => {

      // THEN: the response code should be "401"
      expect(result.code).toBe('401');

      // AND: no token should be returned
      expect(result.data).toBeNull();

      // AND: the token must NOT be stored
      expect(storageSpy.setToken).not.toHaveBeenCalled();
    });

    // Expect one HTTP POST request to the login endpoint
    const req = httpMock.expectOne(`${environment.apiUrl}/auth/login`);

    // Simulate an HTTP 401 Unauthorized error
    req.flush(mockError, { status: 401, statusText: 'Unauthorized' });
  });


  /**
   * Test for Register endpoint call
   */
  it('should call register endpoint correctly', () => {

    // GIVEN: a fake register request payload
    const payload = {
      firstName: 'Ange',           // User first name
      lastName: 'Kamwang',        // User last name
      email: 'ange@mail.com',     // User email
      password: 'password123',   // User password
      confirmPassword: 'password123' // Password confirmation
    };

    // GIVEN: a fake successful register response
    const mockResponse: LogicResult<void> = {
      code: '201',                // Business success code for creation
      message: 'Registration successful', // Success message
      data: null                 // No data returned
    };

    // WHEN: call the register() method
    service.register(payload).subscribe(result => {

      // THEN: the response code should be "201"
      expect(result.code).toBe('201');

      // AND: no data should be returned
      expect(result.data).toBeNull();
    });

    // Expect exactly one HTTP POST request to the register endpoint
    const req = httpMock.expectOne(`${environment.apiUrl}/auth/register`);

    // Verify that the HTTP method is POST
    expect(req.request.method).toBe('POST');

    // Send the fake response to resolve the HTTP call
    req.flush(mockResponse);
  });

});
