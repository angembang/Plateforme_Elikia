import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditWorkshopComponent } from './edit-workshop-component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('EditWorkshopComponent', () => {
  let component: EditWorkshopComponent;
  let fixture: ComponentFixture<EditWorkshopComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        EditWorkshopComponent
      ],
      providers: [
        provideHttpClient(), // Provides HttpClient
        provideHttpClientTesting(), // Mocks HTTP backend
        provideRouter([])  // Mocks Router
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditWorkshopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
