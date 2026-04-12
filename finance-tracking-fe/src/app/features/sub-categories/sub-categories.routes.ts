import { Routes } from '@angular/router';

export const subCategoriesRoutes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/sub-categories-list/sub-categories-list.component').then(
        (m) => m.SubCategoriesListComponent,
      ),
  },
];
