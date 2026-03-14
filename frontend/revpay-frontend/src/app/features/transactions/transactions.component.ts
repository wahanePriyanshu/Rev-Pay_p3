import { Component, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TransactionService, TransactionDto, ActivityItem } from '../../core/services/transaction.service';
import { ProfileService } from '../../core/services/profile.service';

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
  imports: [CommonModule, FormsModule, DatePipe, DecimalPipe],
  templateUrl: './transactions.component.html',
  styleUrls: ['./transactions.component.scss']
})
export class TransactionsComponent implements OnInit {
  allTransactions: UiTransactionRow[] = [];
  filteredTransactions: UiTransactionRow[] = [];
  searchTerm = '';

  currentPage = 1;
  pageSize = 10;

  /** Tabs: 'transactions' | 'activity' */
  activeTab: 'transactions' | 'activity' = 'transactions';

  /** Activity */
  activityItems: ActivityItem[] = [];
  activityLoading = false;

  /** Export: null | 'csv' | 'pdf' */
  exporting: null | 'csv' | 'pdf' = null;

  /** userId → fullName lookup map */
  private userMap = new Map<number, string>();

  get pagedTransactions(): UiTransactionRow[] {
    const startIndex = (this.currentPage - 1) * this.pageSize;
    return this.filteredTransactions.slice(startIndex, startIndex + this.pageSize);
  }

  get totalPages(): number {
    return Math.ceil(this.filteredTransactions.length / this.pageSize) || 1;
  }

  nextPage() { if (this.currentPage < this.totalPages) this.currentPage++; }
  prevPage() { if (this.currentPage > 1) this.currentPage--; }

  constructor(
    private transactionService: TransactionService,
    private profileService: ProfileService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.loadUsersAndThenTransactions();
  }

  setActivityTab(): void {
    this.activeTab = 'activity';
    this.loadActivity();
  }

  // ---- Export ----
  exportCsv(): void {
    this.exporting = 'csv';
    this.transactionService.exportCsv().subscribe({
      next: blob => {
        this.ngZone.run(() => {
          this.downloadBlob(blob, 'transactions.csv', 'text/csv');
          this.exporting = null;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => { this.exporting = null; this.cdr.detectChanges(); });
      }
    });
  }

  exportPdf(): void {
    this.exporting = 'pdf';
    this.transactionService.exportPdf().subscribe({
      next: blob => {
        this.ngZone.run(() => {
          this.downloadBlob(blob, 'transactions.pdf', 'application/pdf');
          this.exporting = null;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => { this.exporting = null; this.cdr.detectChanges(); });
      }
    });
  }

  private downloadBlob(blob: Blob, filename: string, type: string): void {
    const url = URL.createObjectURL(new Blob([blob], { type }));
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    a.click();
    URL.revokeObjectURL(url);
  }

  // ---- Activity ----
  private loadActivity(): void {
    if (this.activityItems.length) return; // already loaded
    this.activityLoading = true;
    this.transactionService.getMyActivity().subscribe({
      next: res => {
        this.ngZone.run(() => {
          this.activityItems = res;
          this.activityLoading = false;
          this.cdr.detectChanges();
        });
      },
      error: () => {
        this.ngZone.run(() => {
          this.activityLoading = false;
          this.cdr.detectChanges();
        });
      }
    });
  }

  // ---- Users ----
  private loadUsersAndThenTransactions(): void {
    this.profileService.getAllUsers().subscribe({
      next: users => {
        this.ngZone.run(() => {
          const myId = Number(localStorage.getItem('userId'));
          const ownName = localStorage.getItem('userName');

          users.forEach((u: any) => {
            const id = u.id as number;
            this.userMap.set(id, u.fullName || u.name || u.email || `User ${id}`);
          });
          if (myId && ownName) this.userMap.set(myId, ownName);

          this.loadTransactions();
        });
      },
      error: () => {
        this.ngZone.run(() => this.loadTransactions());
      }
    });
  }

  private resolveName(userId: number | null | undefined): string {
    if (!userId) return '-';
    return this.userMap.get(userId) || `User ${userId}`;
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
            from: this.resolveName(tx.senderUserId),
            to: this.resolveName(tx.receiverUserId),
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

  getTypeBadgeClass(type: string): string {
    switch (type) {
      case 'SENT':      return 'bg-danger';
      case 'RECEIVED':  return 'bg-success';
      case 'ADDED':     return 'bg-primary';
      case 'WITHDRAWN': return 'bg-warning text-dark';
      default:          return 'bg-secondary';
    }
  }

  private getDisplayType(tx: TransactionDto, currentUserId: number): string {
    if (tx.type === 'ADD_MONEY')  return 'ADDED';
    if (tx.type === 'WITHDRAW')   return 'WITHDRAWN';
    if (tx.type === 'TRANSFER' || tx.type === 'REQUEST_ACCEPTED') {
      return tx.senderUserId === currentUserId ? 'SENT' : 'RECEIVED';
    }
    return tx.type || 'OTHER';
  }
}
