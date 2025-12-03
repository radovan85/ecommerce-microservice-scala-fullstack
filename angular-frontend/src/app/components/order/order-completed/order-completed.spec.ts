import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OrderCompleted } from './order-completed';

describe('OrderCompleted', () => {
  let component: OrderCompleted;
  let fixture: ComponentFixture<OrderCompleted>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OrderCompleted]
    })
    .compileComponents();

    fixture = TestBed.createComponent(OrderCompleted);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
