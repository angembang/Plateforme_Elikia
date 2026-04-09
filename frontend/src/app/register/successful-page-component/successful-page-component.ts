import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';

@Component({
  selector: 'app-successfull-page-component',
  imports: [
    RouterLink,
  ],
  templateUrl: './successful-page-component.html',
  styleUrl: './successful-page-component.scss',
})
export class SuccessfulPageComponent {
  constructor(
    private readonly router: Router
  ) {
  }

  goToHome() {
    this.router.navigate(['']).then(r => {})
  }

}
