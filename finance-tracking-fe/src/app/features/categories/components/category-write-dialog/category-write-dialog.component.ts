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
import { Category } from '../../models/category.model';
import { CategoryService } from '../../services/category.service';

@Component({
  selector: 'pmr-category-write-dialog',
  imports: [Dialog, InputText, Button, FloatLabel, ReactiveFormsModule],
  templateUrl: './category-write-dialog.component.html',
  styleUrl: './category-write-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CategoryWriteDialogComponent {
  private readonly categoryService = inject(CategoryService);
  private readonly fb = inject(FormBuilder);

  readonly visible = model(false);
  readonly category = input<Category>();
  readonly categorySaved = output<Category>();

  protected readonly saving = signal(false);
  protected readonly isEditMode = computed(() => !!this.category());
  protected readonly dialogHeader = computed(() =>
    this.isEditMode() ? 'Edit Category' : 'Add Category',
  );
  protected readonly submitLabel = computed(() => (this.isEditMode() ? 'Save' : 'Create'));

  protected readonly form = this.fb.group({
    code: ['', Validators.required],
    displayName: ['', Validators.required],
  });

  protected onShow(): void {
    const category = this.category();
    if (category) {
      this.form.patchValue({ code: category.code, displayName: category.displayName });
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
    const category = this.category();

    const request$ = category
      ? this.categoryService.update(category.id, payload)
      : this.categoryService.create(payload);

    request$.subscribe({
      next: (saved) => {
        this.categorySaved.emit(saved);
        this.saving.set(false);
        this.close();
      },
      error: () => this.saving.set(false),
    });
  }
}
