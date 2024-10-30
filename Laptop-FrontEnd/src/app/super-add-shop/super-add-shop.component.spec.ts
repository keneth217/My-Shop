import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SuperAddShopComponent } from './super-add-shop.component';

describe('SuperAddShopComponent', () => {
  let component: SuperAddShopComponent;
  let fixture: ComponentFixture<SuperAddShopComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SuperAddShopComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SuperAddShopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
