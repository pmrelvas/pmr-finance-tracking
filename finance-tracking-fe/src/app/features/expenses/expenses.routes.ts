import { Routes } from '@angular/router';

export const expensesRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/expenses-list/expenses-list.component').then(
        (m) => m.ExpensesListComponent,
      ),
  },
];
