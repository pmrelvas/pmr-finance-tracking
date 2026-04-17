import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Menubar } from 'primeng/menubar';
import { MenuItem } from 'primeng/api';

@Component({
  selector: 'app-navbar',
  imports: [Menubar, RouterModule],
  template: `<p-menubar [model]="items" />`,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NavbarComponent {
  protected readonly items: MenuItem[] = [
    { label: 'Categories', routerLink: '/categories' },
    { label: 'Sub-categories', routerLink: '/sub-categories' },
    { label: 'Expenses', routerLink: '/expenses' },
  ];
}
