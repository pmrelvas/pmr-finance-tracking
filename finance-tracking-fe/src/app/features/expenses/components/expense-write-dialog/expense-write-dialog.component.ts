import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  input,
  model,
  OnInit,
  output,
  signal,
} from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { InputText } from 'primeng/inputtext';
import { InputNumber } from 'primeng/inputnumber';
import { Button } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { FloatLabel } from 'primeng/floatlabel';
import { Select } from 'primeng/select';
import { DatePicker } from 'primeng/datepicker';
import { Category, SubCategory } from '../../../categories/models/category.model';
import { CategoryService } from '../../../categories/services/category.service';
import { Expense, ExpensePayload, ExpenseType } from '../../models/expense.model';
import { ExpenseService } from '../../services/expense.service';

@Component({
  selector: 'pmr-expense-write-dialog',
  imports: [
    Dialog,
    InputText,
    InputNumber,
    Button,
    FloatLabel,
    Select,
    DatePicker,
    ReactiveFormsModule,
  ],
  templateUrl: './expense-write-dialog.component.html',
  styleUrl: './expense-write-dialog.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExpenseWriteDialogComponent implements OnInit {
  private readonly expenseService = inject(ExpenseService);
  private readonly categoryService = inject(CategoryService);
  private readonly fb = inject(FormBuilder);

  readonly visible = model(false);
  readonly expense = input<Expense>();
  readonly expenseSaved = output<Expense>();

  protected readonly saving = signal(false);
  protected readonly categories = signal<Category[]>([]);
  protected readonly selectedCategoryId = signal<string>('');

  protected readonly availableSubCategories = computed<SubCategory[]>(() =>
    this.categories().find((c) => c.id === this.selectedCategoryId())?.subCategories ?? [],
  );

  protected readonly isEditMode = computed(() => !!this.expense());
  protected readonly dialogHeader = computed(() =>
    this.isEditMode() ? 'Edit Expense' : 'Add Expense',
  );
  protected readonly submitLabel = computed(() => (this.isEditMode() ? 'Save' : 'Create'));

  protected readonly typeOptions: { label: string; value: ExpenseType }[] = [
    { label: 'Debit', value: 'DEBIT' },
    { label: 'Credit', value: 'CREDIT' },
  ];

  protected readonly form = this.fb.group({
    operationDate: [null as Date | null, Validators.required],
    description: ['', Validators.required],
    value: [null as number | null, Validators.required],
    type: [null as ExpenseType | null, Validators.required],
    categoryId: ['', Validators.required],
    subCategoryId: ['' as string | null],
    source: ['', Validators.required],
  });

  ngOnInit(): void {
    this.categoryService.getAll().subscribe({
      next: (data) => this.categories.set(data),
    });
  }

  protected onShow(): void {
    const expense = this.expense();
    if (expense) {
      this.selectedCategoryId.set(expense.category.id);
      this.form.patchValue({
        operationDate: new Date(expense.operationDate),
        description: expense.description,
        value: expense.value,
        type: expense.type,
        categoryId: expense.category.id,
        subCategoryId: expense.subCategory?.id ?? null,
        source: expense.source,
      });
    } else {
      this.selectedCategoryId.set('');
      this.form.reset();
    }
  }

  protected onCategoryChange(categoryId: string): void {
    this.selectedCategoryId.set(categoryId);
    this.form.controls.subCategoryId.reset(null);
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
    const { operationDate, description, value, type, categoryId, subCategoryId, source } =
      this.form.getRawValue();

    const payload: ExpensePayload = {
      operationDate: operationDate!.toISOString(),
      description: description!,
      value: value!,
      type: type!,
      categoryId: categoryId!,
      subCategoryId: subCategoryId ?? undefined,
      source: source!,
    };

    const expense = this.expense();
    const request$ = expense
      ? this.expenseService.update(expense.id, payload)
      : this.expenseService.create(payload);

    request$.subscribe({
      next: (saved) => {
        this.expenseSaved.emit(saved);
        this.saving.set(false);
        this.close();
      },
      error: () => this.saving.set(false),
    });
  }
}
