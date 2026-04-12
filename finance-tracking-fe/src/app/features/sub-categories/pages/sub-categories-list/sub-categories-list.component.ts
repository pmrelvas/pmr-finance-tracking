import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { InputText } from 'primeng/inputtext';
import { Tag } from 'primeng/tag';
import { Button } from 'primeng/button';
import { Select } from 'primeng/select';
import { FormsModule } from '@angular/forms';
import { Category, SubCategory } from '../../../categories/models/category.model';
import { CategoryService } from '../../../categories/services/category.service';
import { SubCategoryService } from '../../services/sub-category.service';
import { SubCategoryWriteDialogComponent } from '../../components/sub-category-write-dialog/sub-category-write-dialog.component';
import { DATE_FORMATS } from '../../../../core/constants/date-formats';

@Component({
  selector: 'pmr-sub-categories-list',
  imports: [
    TableModule,
    InputText,
    Tag,
    DatePipe,
    Button,
    Select,
    FormsModule,
    SubCategoryWriteDialogComponent,
  ],
  templateUrl: './sub-categories-list.component.html',
  styleUrl: './sub-categories-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubCategoriesListComponent implements OnInit {
  private readonly categoryService = inject(CategoryService);
  private readonly subCategoryService = inject(SubCategoryService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  protected readonly dateFormats = DATE_FORMATS;

  protected readonly categories = signal<Category[]>([]);
  protected readonly selectedCategory = signal<Category | null>(null);
  protected readonly subCategories = signal<SubCategory[]>([]);
  protected readonly loadingCategories = signal(true);
  protected readonly loadingSubCategories = signal(false);
  protected readonly dialogVisible = signal(false);
  protected readonly selectedSubCategory = signal<SubCategory | undefined>(undefined);

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => {
        this.categories.set(data);
        this.loadingCategories.set(false);

        const categoryId = this.route.snapshot.queryParamMap.get('categoryId');
        if (categoryId) {
          const match = data.find((c) => c.id === categoryId) ?? null;
          this.selectedCategory.set(match);
          if (match) this.loadSubCategories(match.id);
        }
      },
      error: () => this.loadingCategories.set(false),
    });
  }

  protected onCategoryChange(category: Category | null): void {
    this.selectedCategory.set(category);
    this.subCategories.set([]);

    if (!category) {
      this.router.navigate([], { queryParams: {} });
      return;
    }

    this.router.navigate([], { queryParams: { categoryId: category.id } });
    this.loadSubCategories(category.id);
  }

  protected openCreateDialog(): void {
    this.selectedSubCategory.set(undefined);
    this.dialogVisible.set(true);
  }

  protected openEditDialog(subCategory: SubCategory): void {
    this.selectedSubCategory.set(subCategory);
    this.dialogVisible.set(true);
  }

  protected onSubCategorySaved(saved: SubCategory): void {
    this.subCategories.update((list) => {
      const index = list.findIndex((s) => s.id === saved.id);
      return index >= 0 ? list.map((s) => (s.id === saved.id ? saved : s)) : [...list, saved];
    });
  }

  private loadSubCategories(categoryId: string): void {
    this.loadingSubCategories.set(true);
    this.subCategoryService.getAll(categoryId).subscribe({
      next: (data) => {
        this.subCategories.set(data);
        this.loadingSubCategories.set(false);
      },
      error: () => this.loadingSubCategories.set(false),
    });
  }
}
