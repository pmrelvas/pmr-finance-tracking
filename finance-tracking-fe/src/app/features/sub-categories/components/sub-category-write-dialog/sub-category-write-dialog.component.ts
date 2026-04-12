import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  input,
  model,
  output,
  signal,
} from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { InputText } from 'primeng/inputtext';
import { Button } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { FloatLabel } from 'primeng/floatlabel';
import { SubCategory } from '../../../categories/models/category.model';
import { SubCategoryService } from '../../services/sub-category.service';

@Component({
  selector: 'pmr-sub-category-write-dialog',
  imports: [Dialog, InputText, Button, FloatLabel, ReactiveFormsModule],
  templateUrl: './sub-category-write-dialog.component.html',
  styleUrl: './sub-category-write-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubCategoryWriteDialogComponent {
  private readonly subCategoryService = inject(SubCategoryService);
  private readonly fb = inject(FormBuilder);

  readonly visible = model(false);
  readonly categoryId = input.required<string>();
  readonly subCategory = input<SubCategory>();
  readonly subCategorySaved = output<SubCategory>();

  protected readonly saving = signal(false);
  protected readonly isEditMode = computed(() => !!this.subCategory());
  protected readonly dialogHeader = computed(() =>
    this.isEditMode() ? 'Edit Sub-category' : 'Add Sub-category',
  );
  protected readonly submitLabel = computed(() => (this.isEditMode() ? 'Save' : 'Create'));

  protected readonly form = this.fb.group({
    code: ['', Validators.required],
    displayName: ['', Validators.required],
  });

  protected onShow(): void {
    const subCategory = this.subCategory();
    if (subCategory) {
      this.form.patchValue({ code: subCategory.code, displayName: subCategory.displayName });
    } else {
      this.form.reset();
    }
  }

  protected close(): void {
    this.visible.set(false);
  }

  protected onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    const { code, displayName } = this.form.getRawValue();
    const payload = { code: code!, displayName: displayName! };
    const subCategory = this.subCategory();

    const request$ = subCategory
      ? this.subCategoryService.update(this.categoryId(), subCategory.id, payload)
      : this.subCategoryService.create(this.categoryId(), payload);

    request$.subscribe({
      next: (saved) => {
        this.subCategorySaved.emit(saved);
        this.saving.set(false);
        this.close();
      },
      error: () => this.saving.set(false),
    });
  }
}
