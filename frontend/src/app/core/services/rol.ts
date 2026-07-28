import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Rol {
  id: string;
  nombre: string;
}

@Injectable({
  providedIn: 'root',
})
export class RolService {
  private http = inject(HttpClient);
  private apiURL = `${environment.apiUrl}/roles`;

  getRoles(): Observable<Rol[]> {
    return this.http.get<Rol[]>(this.apiURL);
  }
}
