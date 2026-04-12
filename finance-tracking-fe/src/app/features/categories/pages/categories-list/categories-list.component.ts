import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { InputText } from 'primeng/inputtext';
import { Tag } from 'primeng/tag';
import { Button } from 'primeng/button';
import { Category } from '../../models/category.model';
import { CategoryService } from '../../services/category.service';
import { DATE_FORMATS } from '../../../../core/constants/date-formats';
import { CategoryWriteDialogComponent } from '../../components/category-write-dialog/category-write-dialog.component';

@Component({
  selector: 'pmr-categories-list',
  imports: [TableModule, InputText, Tag, DatePipe, Button, CategoryWriteDialogComponent],
  templateUrl: './categories-list.component.html',
  styleUrl: './categories-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategoriesListComponent implements OnInit {
  private readonly categoryService = inject(CategoryService);

  protected readonly dateFormats = DATE_FORMATS;

  protected readonly categories = signal<Category[]>([]);
  protected readonly loading = signal(true);
  protected readonly dialogVisible = signal(false);
  protected readonly selectedCategory = signal<Category | undefined>(undefined);

  ngOnInit(): void {
    this.loadCategories();
  }

  protected openCreateDialog(): void {
    this.selectedCategory.set(undefined);
    this.dialogVisible.set(true);
  }

  protected openEditDialog(category: Category): void {
    this.selectedCategory.set(category);
    this.dialogVisible.set(true);
  }

  protected onCategorySaved(saved: Category): void {
    this.categories.update((list) => {
      const index = list.findIndex((c) => c.id === saved.id);
      return index >= 0 ? list.map((c) => (c.id === saved.id ? saved : c)) : [...list, saved];
    });
  }

  private loadCategories(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => {
        this.categories.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
