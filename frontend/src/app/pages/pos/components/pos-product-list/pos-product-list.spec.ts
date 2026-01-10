import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PosProductList } from './pos-product-list';

describe('PosProductList', () => {
  let component: PosProductList;
  let fixture: ComponentFixture<PosProductList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PosProductList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PosProductList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
