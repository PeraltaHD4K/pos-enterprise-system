import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PosMovementModal } from './pos-movement-modal';

describe('PosMovementModal', () => {
  let component: PosMovementModal;
  let fixture: ComponentFixture<PosMovementModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PosMovementModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PosMovementModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
