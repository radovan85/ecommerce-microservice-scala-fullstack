import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProductUpdateForm } from './product-update-form';

describe('ProductUpdateForm', () => {
  let component: ProductUpdateForm;
  let fixture: ComponentFixture<ProductUpdateForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ProductUpdateForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProductUpdateForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
