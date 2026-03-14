import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransactionService, TransactionDto } from '../../core/services/transaction.service';

interface UiTransactionRow {
  id: number;
  type: string;
  status: string;
  from: string;
  to: string;
  amount: number;
  note: string;
  createdAt: string;
}

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.scss']
})
export class TransactionsComponent implements OnInit {
  allTransactions: UiTransactionRow[] = [];
  filteredTransactions: UiTransactionRow[] = [];
  searchTerm = '';

  currentPage = 1;
  pageSize = 10;

  get pagedTransactions(): UiTransactionRow[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.filteredTransactions.slice(startIndex, startIndex + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredTransactions.length / this.pageSize) || 1;
  }

  nextPage() {
    if (this.currentPage < this.totalPages) {
      this.currentPage++;
    }
  }

  prevPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
    }
  }

  constructor(
    private transactionService: TransactionService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.loadTransactions();
  }

  private loadTransactions() {
    this.transactionService.getMyTransactions().subscribe({
      next: res => {
        this.ngZone.run(() => {
          const currentUserId = Number(localStorage.getItem('userId')) || 0;
          this.allTransactions = res.map((tx: TransactionDto) => ({
            id: tx.id,
            type: this.getDisplayType(tx, currentUserId),
            status: tx.status || 'COMPLETED',
            from: tx.senderUserId ? `User ${tx.senderUserId}` : '-',
            to: tx.receiverUserId ? `User ${tx.receiverUserId}` : '-',
            amount: tx.amount,
            note: tx.description || '-',
            createdAt: new Date(tx.createdAt).toLocaleString()
          }));
          this.filteredTransactions = [...this.allTransactions];
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => {
          this.allTransactions = [];
          this.filteredTransactions = [];
          this.cdr.detectChanges();
        });
      }
    });
  }

  applySearch() {
    const term = this.searchTerm.toLowerCase();
    this.filteredTransactions = this.allTransactions.filter(tx =>
      tx.from.toLowerCase().includes(term) ||
      tx.to.toLowerCase().includes(term) ||
      tx.note.toLowerCase().includes(term) ||
      tx.status.toLowerCase().includes(term) ||
      tx.type.toLowerCase().includes(term)
    );
    this.currentPage = 1;
  }

  getDisplayType(tx: TransactionDto, currentUserId: number): string {
    if (tx.type === 'TRANSFER') return tx.senderUserId === currentUserId ? 'SENT' : 'RECEIVED';
    if (tx.type === 'REQUEST_ACCEPTED') return tx.senderUserId === currentUserId ? 'PAID REQUEST' : 'RECEIVED PAYMENT';
    if (tx.type === 'ADD_MONEY') return 'ADD MONEY';
    if (tx.type === 'WITHDRAW') return 'WITHDRAW';
    return tx.type;
  }

  getTypeBadgeClass(type: string): string {
    if (type === 'SENT' || type.includes('PAID')) return 'bg-danger';
    if (type === 'RECEIVED' || type.includes('RECEIVED PAYMENT')) return 'bg-success';
    if (type === 'ADD MONEY') return 'bg-primary';
    if (type === 'WITHDRAW') return 'bg-warning text-dark';
    return 'bg-secondary';
  }
}

