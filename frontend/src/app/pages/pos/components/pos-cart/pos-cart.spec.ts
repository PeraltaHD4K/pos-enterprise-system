import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PosCart } from './pos-cart';

describe('PosCart', () => {
  let component: PosCart;
  let fixture: ComponentFixture<PosCart>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PosCart]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PosCart);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
