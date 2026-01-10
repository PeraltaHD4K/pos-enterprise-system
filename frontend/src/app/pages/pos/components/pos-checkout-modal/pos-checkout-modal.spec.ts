import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PosCheckoutModal } from './pos-checkout-modal';

describe('PosCheckoutModal', () => {
  let component: PosCheckoutModal;
  let fixture: ComponentFixture<PosCheckoutModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PosCheckoutModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PosCheckoutModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
