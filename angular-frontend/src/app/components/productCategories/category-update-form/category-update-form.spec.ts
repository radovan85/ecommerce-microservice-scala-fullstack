import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryUpdateForm } from './category-update-form';

describe('CategoryUpdateForm', () => {
  let component: CategoryUpdateForm;
  let fixture: ComponentFixture<CategoryUpdateForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryUpdateForm]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CategoryUpdateForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
