import { NgModule } from '@angular/core';
import { CartBaseModule } from "@spartacus/cart/base";
import { CpqQuoteModule } from "@spartacus/cpq-quote";
import { EstimatedDeliveryDateModule } from "@spartacus/estimated-delivery-date";

@NgModule({
  declarations: [],
  imports: [
    CartBaseModule,
    CpqQuoteModule,
    EstimatedDeliveryDateModule
  ]
})
export class CartBaseWrapperModule { }
