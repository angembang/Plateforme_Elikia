import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListEventComponent } from './list-event-component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideRouter} from '@angular/router';

describe('ListEventComponent', () => {
  let component: ListEventComponent;
  let fixture: ComponentFixture<ListEventComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        ListEventComponent
      ],
      providers: [
      provideHttpClient(), // Provides HttpClient
      provideHttpClientTesting(), // Mocks HTTP backend
      provideRouter([])  // Mocks Router
    ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListEventComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
