import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PosHistoryModal } from './pos-history-modal';

describe('PosHistoryModal', () => {
  let component: PosHistoryModal;
  let fixture: ComponentFixture<PosHistoryModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PosHistoryModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PosHistoryModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
