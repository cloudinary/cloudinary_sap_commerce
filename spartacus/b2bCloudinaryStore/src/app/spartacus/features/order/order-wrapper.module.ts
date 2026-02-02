import { NgModule } from '@angular/core';
import { OmfOrderModule } from '@spartacus/omf/order';
import { OpfOrderModule } from '@spartacus/opf/order';
import { OrderModule } from '@spartacus/order';

@NgModule({
  declarations: [],
  imports: [OrderModule, OpfOrderModule, OmfOrderModule],
})
export class OrderWrapperModule {}
