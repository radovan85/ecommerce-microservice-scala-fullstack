import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddressConfirmation } from './address-confirmation';

describe('AddressConfirmation', () => {
  let component: AddressConfirmation;
  let fixture: ComponentFixture<AddressConfirmation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddressConfirmation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddressConfirmation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
