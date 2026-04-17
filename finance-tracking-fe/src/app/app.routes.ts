import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'categories', pathMatch: 'full' },
  {
    path: 'categories',
    loadChildren: () =>
      import('./features/categories/categories.routes').then((m) => m.categoriesRoutes),
  },
  {
    path: 'sub-categories',
    loadChildren: () =>
      import('./features/sub-categories/sub-categories.routes').then((m) => m.subCategoriesRoutes),
  },
  {
    path: 'expenses',
    loadChildren: () =>
      import('./features/expenses/expenses.routes').then((m) => m.expensesRoutes),
  },
];
