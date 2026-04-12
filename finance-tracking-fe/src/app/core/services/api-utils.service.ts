import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';

@Injectable({ providedIn: 'root' })
export class ApiUtilsService {
  buildParams(params: Record<string, string | number | boolean | null | undefined>): HttpParams {
    return Object.entries(params).reduce((httpParams, [key, value]) => {
      if (value === null || value === undefined || value === '') {
        return httpParams;
      }
      return httpParams.set(key, String(value));
    }, new HttpParams());
  }
}
