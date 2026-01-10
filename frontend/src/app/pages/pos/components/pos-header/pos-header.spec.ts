import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PosHeader } from './pos-header';

describe('PosHeader', () => {
  let component: PosHeader;
  let fixture: ComponentFixture<PosHeader>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PosHeader]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PosHeader);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
