import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PosTicketModal } from './pos-ticket-modal';

describe('PosTicketModal', () => {
  let component: PosTicketModal;
  let fixture: ComponentFixture<PosTicketModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PosTicketModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PosTicketModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
