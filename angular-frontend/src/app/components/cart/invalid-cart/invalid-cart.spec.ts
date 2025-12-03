import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvalidCart } from './invalid-cart';

describe('InvalidCart', () => {
  let component: InvalidCart;
  let fixture: ComponentFixture<InvalidCart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvalidCart]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvalidCart);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
