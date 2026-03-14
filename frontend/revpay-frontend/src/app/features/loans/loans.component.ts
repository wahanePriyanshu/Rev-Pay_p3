import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { LoanService, LoanDto, LoanAnalytics, LoanRepayment } from '../../core/services/loan.service';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './loans.component.html',
  styleUrls: ['./loans.component.scss']
})
export class LoansComponent implements OnInit {
  loans: LoanDto[] = [];
  analytics: LoanAnalytics | null = null;
  loading = false;
  applying = false;
  loanForm!: FormGroup;

  // Repayments panel
  selectedLoan: LoanDto | null = null;
  repayments: LoanRepayment[] = [];
  repaymentsLoading = false;
  showRepayModal = false;
  repayAmount = 0;
  repayNote = '';
  repaying = false;

  // Loan type options (from backend enum)
  loanTypes = ['BUSINESS', 'WORKING_CAPITAL', 'EQUIPMENT', 'EXPANSION', 'PERSONAL', 'HOME', 'AUTO', 'EDUCATION', 'MEDICAL'];

  constructor(
    private loanService: LoanService,
    private fb: FormBuilder,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.buildForm();
    this.loadLoans();
    this.loadAnalytics();
  }

  private buildForm() {
    this.loanForm = this.fb.group({
      businessName: ['', Validators.required],
      loanType: ['BUSINESS', Validators.required],
      amount: [null, [Validators.required, Validators.min(1000)]],
      interestRate: [10, [Validators.required, Validators.min(1), Validators.max(100)]],
      tenureMonths: [12, [Validators.required, Validators.min(1)]],
      purpose: ['', Validators.required]
    });
  }

  loadLoans() {
    this.loading = true;
    this.loanService.getLoans().subscribe({
      next: res => { this.loans = res; this.loading = false; },
      error: () => { this.loading = false; }
    });
  }

  loadAnalytics() {
    this.loanService.getAnalytics().subscribe({
      next: res => this.analytics = res,
      error: () => {}
    });
  }

  submitLoan() {
    if (this.loanForm.invalid) { this.loanForm.markAllAsTouched(); return; }
    this.applying = true;
    this.loanService.createLoan(this.loanForm.value).subscribe({
      next: () => {
        this.applying = false;
        this.toast.show('Loan application submitted successfully', 'success');
        this.loanForm.reset({ loanType: 'BUSINESS', interestRate: 10, tenureMonths: 12 });
        this.loadLoans();
        this.loadAnalytics();
      },
      error: () => {
        this.applying = false;
        this.toast.show('Failed to submit loan application', 'error');
      }
    });
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'bg-warning text-dark',
      APPROVED: 'bg-success',
      REJECTED: 'bg-danger',
      CLOSED: 'bg-secondary'
    };
    return map[status] || 'bg-secondary';
  }

  // ===== Repayments =====
  openRepayments(loan: LoanDto) {
    this.selectedLoan = loan;
    this.repayments = [];
    this.repaymentsLoading = true;
    this.loanService.getRepayments(loan.id).subscribe({
      next: res => { this.repayments = res; this.repaymentsLoading = false; },
      error: () => { this.repaymentsLoading = false; }
    });
  }

  openRepayModal() {
    this.repayAmount = 0;
    this.repayNote = '';
    this.showRepayModal = true;
  }

  submitRepayment() {
    if (!this.selectedLoan || this.repayAmount <= 0) return;
    this.repaying = true;
    this.loanService.addRepayment(this.selectedLoan.id, { amount: this.repayAmount, note: this.repayNote }).subscribe({
      next: (rep) => {
        this.repayments.push(rep);
        this.showRepayModal = false;
        this.repaying = false;
        this.toast.show('Repayment recorded', 'success');
        this.loadAnalytics();
      },
      error: () => {
        this.repaying = false;
        this.toast.show('Failed to record repayment', 'error');
      }
    });
  }
}
