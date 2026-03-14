import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface LoanDto {
  id: number;
  loanNumber: string;
  businessName: string;
  loanType: string;
  amount: number;
  interestRate: number;
  tenureMonths: number;
  monthlyEmi: number;
  purpose: string;
  status: string;
  appliedAt: string;
  approvedAt: string | null;
  rejectedAt: string | null;
}

export interface CreateLoanPayload {
  businessName: string;
  loanType: string;
  amount: number;
  interestRate: number;
  tenureMonths: number;
  purpose: string;
}

export interface LoanAnalytics {
  totalLoans: number;
  totalBorrowed: number;
  totalRepaid: number;
  outstandingAmount: number;
}

export interface LoanRepayment {
  id: number;
  loanId: number;
  amount: number;
  paidAt: string;
  note: string;
}

export interface CreateRepaymentPayload {
  amount: number;
  note: string;
}

@Injectable({ providedIn: 'root' })
export class LoanService {
  private readonly API = environment.apiBaseUrl;

  constructor(private http: HttpClient) {}

  getLoans(): Observable<LoanDto[]> {
    return this.http.get<LoanDto[]>(`${this.API}/loans`);
  }

  getLoanById(id: number): Observable<LoanDto> {
    return this.http.get<LoanDto>(`${this.API}/loans/${id}`);
  }

  createLoan(payload: CreateLoanPayload): Observable<LoanDto> {
    return this.http.post<LoanDto>(`${this.API}/loans`, payload);
  }

  getAnalytics(): Observable<LoanAnalytics> {
    return this.http.get<LoanAnalytics>(`${this.API}/loans/analytics`);
  }

  getRepayments(loanId: number): Observable<LoanRepayment[]> {
    return this.http.get<LoanRepayment[]>(`${this.API}/loans/${loanId}/repayments`);
  }

  addRepayment(loanId: number, payload: CreateRepaymentPayload): Observable<LoanRepayment> {
    return this.http.post<LoanRepayment>(`${this.API}/loans/${loanId}/repayments`, payload);
  }
}
