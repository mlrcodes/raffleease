import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Raffle } from '../../models/raffles/raffle';
import { environment } from '../../../../environments/environment';
import { SuccessResponse } from '../../models/responses/success-response';
import { RaffleEdit } from '../../models/raffles/raffle-edit';
import { RaffleCreate } from '../../models/raffles/raffle-create';

@Injectable({
  providedIn: 'root'
})
export class RafflesService {

  constructor(
    private httpClient: HttpClient
  ) { }

  private baseURL: string = `${environment.serverPath}/api/v1/raffles`;

  create(request: RaffleCreate): Observable<SuccessResponse<Raffle>> {
    return this.httpClient.post(`${this.baseURL}`, request) as Observable<SuccessResponse<Raffle>>;
  }

  get(id: number): Observable<SuccessResponse<Raffle>> {
    return this.httpClient.get(`${this.baseURL}/${id}`) as Observable<SuccessResponse<Raffle>>;
  }

  getAll(): Observable<SuccessResponse<Raffle[]>> {
    return this.httpClient.get(`${this.baseURL}`) as Observable<SuccessResponse<Raffle[]>>;
  }

  delete(id: number): Observable<void> {
    return this.httpClient.delete<void>(`${this.baseURL}/${id}`) as Observable<void>;
  }

  publish(id: number): Observable<SuccessResponse<Raffle>> {
    return this.httpClient.patch(`${this.baseURL}/${id}/publish`, {}) as Observable<SuccessResponse<Raffle>>;
  }

  pause(id: number): Observable<SuccessResponse<Raffle>> {
    return this.httpClient.patch(`${this.baseURL}/${id}/pause`, {}) as Observable<SuccessResponse<Raffle>>;
  }

  restart(id: number): Observable<SuccessResponse<Raffle>> {
    return this.httpClient.patch(`${this.baseURL}/${id}/restart`, {}) as Observable<SuccessResponse<Raffle>>;
  }

  edit(raffleId: number, raffleEdit: Partial<RaffleEdit>) {
    return this.httpClient.put(`${this.baseURL}/${raffleId}`, raffleEdit) as Observable<SuccessResponse<Raffle>>;
  }
}
