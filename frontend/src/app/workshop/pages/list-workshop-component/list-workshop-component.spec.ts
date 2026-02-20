import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListWorkshopComponent } from './list-workshop-component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('ListWorkshopComponent', () => {
  let component: ListWorkshopComponent;
  let fixture: ComponentFixture<ListWorkshopComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ListWorkshopComponent
      ],
      providers: [
        provideHttpClient(), // Provides HttpClient
        provideHttpClientTesting(), // Mocks HTTP backend
        provideRouter([])  // Mocks Router
      ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListWorkshopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
