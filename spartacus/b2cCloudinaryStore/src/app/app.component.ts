import { Component } from '@angular/core';
import { StorefrontComponent } from "@spartacus/storefront";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
  imports: [StorefrontComponent]
})
export class AppComponent {
  title = 'b2cCloudinaryStore';
}
