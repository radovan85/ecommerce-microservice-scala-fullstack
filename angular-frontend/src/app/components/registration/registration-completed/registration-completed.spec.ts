import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistrationCompleted } from './registration-completed';

describe('RegistrationCompleted', () => {
  let component: RegistrationCompleted;
  let fixture: ComponentFixture<RegistrationCompleted>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistrationCompleted]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistrationCompleted);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
