import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhoneConfirmation } from './phone-confirmation';

describe('PhoneConfirmation', () => {
  let component: PhoneConfirmation;
  let fixture: ComponentFixture<PhoneConfirmation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PhoneConfirmation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PhoneConfirmation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
