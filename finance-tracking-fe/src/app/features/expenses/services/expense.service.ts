import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Expense, ExpensePayload } from '../models/expense.model';
import { ApiUtilsService } from '../../../core/services/api-utils.service';

@Injectable({ providedIn: 'root' })
export class ExpenseService {
  private readonly http = inject(HttpClient);
  private readonly apiUtils = inject(ApiUtilsService);
  private readonly baseUrl = 'http://localhost:8080/api/v1/expenses';

  getAll(searchTerm?: string): Observable<Expense[]> {
    const params = this.apiUtils.buildParams({ searchTerm });
    return this.http.get<Expense[]>(this.baseUrl, { params });
  }

  create(payload: ExpensePayload): Observable<Expense> {
    return this.http.post<Expense>(this.baseUrl, payload);
  }

  update(id: string, payload: ExpensePayload): Observable<Expense> {
    return this.http.put<Expense>(`${this.baseUrl}/${id}`, payload);
  }
}
