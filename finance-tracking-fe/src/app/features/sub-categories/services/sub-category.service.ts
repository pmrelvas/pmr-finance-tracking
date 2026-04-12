import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SubCategory } from '../../categories/models/category.model';
import { ApiUtilsService } from '../../../core/services/api-utils.service';

@Injectable({ providedIn: 'root' })
export class SubCategoryService {
  private readonly http = inject(HttpClient);
  private readonly apiUtils = inject(ApiUtilsService);
  private readonly baseUrl = 'http://localhost:8080/api/v1/categories';

  private url(categoryId: string): string {
    return `${this.baseUrl}/${categoryId}/sub-categories`;
  }

  getAll(categoryId: string, searchTerm?: string): Observable<SubCategory[]> {
    const params = this.apiUtils.buildParams({ searchTerm });
    return this.http.get<SubCategory[]>(this.url(categoryId), { params });
  }

  create(categoryId: string, payload: Pick<SubCategory, 'code' | 'displayName'>): Observable<SubCategory> {
    return this.http.post<SubCategory>(this.url(categoryId), payload);
  }

  update(categoryId: string, id: string, payload: Pick<SubCategory, 'code' | 'displayName'>): Observable<SubCategory> {
    return this.http.put<SubCategory>(`${this.url(categoryId)}/${id}`, payload);
  }
}
