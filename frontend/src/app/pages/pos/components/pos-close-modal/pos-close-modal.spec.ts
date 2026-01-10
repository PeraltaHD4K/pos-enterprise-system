import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PosCloseModal } from './pos-close-modal';

describe('PosCloseModal', () => {
  let component: PosCloseModal;
  let fixture: ComponentFixture<PosCloseModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PosCloseModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PosCloseModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
