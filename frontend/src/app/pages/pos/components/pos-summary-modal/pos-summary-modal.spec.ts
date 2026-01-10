import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PosSummaryModal } from './pos-summary-modal';

describe('PosSummaryModal', () => {
  let component: PosSummaryModal;
  let fixture: ComponentFixture<PosSummaryModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PosSummaryModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PosSummaryModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
