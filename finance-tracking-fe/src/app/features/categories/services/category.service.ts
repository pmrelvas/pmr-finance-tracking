import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category } from '../models/category.model';
import { ApiUtilsService } from '../../../core/services/api-utils.service';

@Injectable({ providedIn: 'root' })
export class CategoryService {
  private readonly http = inject(HttpClient);
  private readonly apiUtils = inject(ApiUtilsService);
  private readonly baseUrl = 'http://localhost:8080/api/v1/categories';

  getAll(searchTerm?: string): Observable<Category[]> {
    const params = this.apiUtils.buildParams({ searchTerm });
    return this.http.get<Category[]>(this.baseUrl, { params });
  }

  create(payload: Pick<Category, 'code' | 'displayName'>): Observable<Category> {
    return this.http.post<Category>(this.baseUrl, payload);
  }

  update(id: string, payload: Pick<Category, 'code' | 'displayName'>): Observable<Category> {
    return this.http.put<Category>(`${this.baseUrl}/${id}`, payload);
  }
}
