import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistrationFailed } from './registration-failed';

describe('RegistrationFailed', () => {
  let component: RegistrationFailed;
  let fixture: ComponentFixture<RegistrationFailed>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistrationFailed]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistrationFailed);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
