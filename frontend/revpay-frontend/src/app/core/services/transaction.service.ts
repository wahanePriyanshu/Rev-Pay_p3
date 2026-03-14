import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface TransactionDto {
  id: number;
  transactionRef: string;
  type: string;
  senderUserId: number;
  receiverUserId: number;
  amount: number;
  status: string;
  description: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly API = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getMyTransactions(): Observable<TransactionDto[]> {
    return this.http.get<TransactionDto[]>(`${this.API}/transactions/my`);
  }
}

