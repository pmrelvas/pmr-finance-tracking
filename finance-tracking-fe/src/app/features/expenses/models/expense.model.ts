import { Category, SubCategory } from '../../categories/models/category.model';

export type ExpenseType = 'DEBIT' | 'CREDIT';

export interface Expense {
  id: string;
  operationDate: string;
  description: string;
  value: number;
  type: ExpenseType;
  category: Category;
  subCategory?: SubCategory;
  source: string;
  createdAt: string;
  updatedAt: string;
}

export interface ExpensePayload {
  operationDate: string;
  description: string;
  value: number;
  type: ExpenseType;
  categoryId: string;
  subCategoryId?: string;
  source: string;
}
