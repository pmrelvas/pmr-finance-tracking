import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { TableModule } from 'primeng/table';
import { InputText } from 'primeng/inputtext';
import { Tag } from 'primeng/tag';
import { Button } from 'primeng/button';
import { Expense } from '../../models/expense.model';
import { ExpenseService } from '../../services/expense.service';
import { DATE_FORMATS } from '../../../../core/constants/date-formats';
import { ExpenseWriteDialogComponent } from '../../components/expense-write-dialog/expense-write-dialog.component';

@Component({
  selector: 'pmr-expenses-list',
  imports: [TableModule, InputText, Tag, DatePipe, DecimalPipe, Button, ExpenseWriteDialogComponent],
  templateUrl: './expenses-list.component.html',
  styleUrl: './expenses-list.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExpensesListComponent implements OnInit {
  private readonly expenseService = inject(ExpenseService);

  protected readonly dateFormats = DATE_FORMATS;

  protected readonly expenses = signal<Expense[]>([]);
  protected readonly loading = signal(true);
  protected readonly dialogVisible = signal(false);
  protected readonly selectedExpense = signal<Expense | undefined>(undefined);

  ngOnInit(): void {
    this.loadExpenses();
  }

  protected openCreateDialog(): void {
    this.selectedExpense.set(undefined);
    this.dialogVisible.set(true);
  }

  protected openEditDialog(expense: Expense): void {
    this.selectedExpense.set(expense);
    this.dialogVisible.set(true);
  }

  protected onExpenseSaved(saved: Expense): void {
    this.expenses.update((list) => {
      const index = list.findIndex((e) => e.id === saved.id);
      return index >= 0 ? list.map((e) => (e.id === saved.id ? saved : e)) : [...list, saved];
    });
  }

  private loadExpenses(): void {
    this.expenseService.getAll().subscribe({
      next: (data) => {
        this.expenses.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
