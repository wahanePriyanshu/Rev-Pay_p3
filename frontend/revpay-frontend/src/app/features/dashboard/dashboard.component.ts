import { Component, OnInit, signal, computed, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { WalletService } from '../../core/services/wallet.service';
import { TransactionService, TransactionDto } from '../../core/services/transaction.service';
import { TransferService } from '../../core/services/transfer.service';
import { ProfileService } from '../../core/services/profile.service';

interface UiTransaction {
  id: string;
  date: string;
  description: string;
  amount: number;
  type: 'credit' | 'debit';
  createdAt: string;
}

interface UserRecipient {
  id: number;
  name: string;
  email: string;
  avatarColor: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  userName = 'User';
  userEmail = 'Logged in user';
  walletCurrency = 'INR';
  accountType: string = '';

  get isPersonal(): boolean {
    return this.accountType === 'PERSONAL';
  }

  get isBusiness(): boolean {
    return this.accountType === 'BUSINESS';
  }

  // ---- Signals ----
  private walletBalanceSignal = signal<number>(0);
  walletBalance = computed(() => this.walletBalanceSignal());

  private transactionsSignal = signal<UiTransaction[]>([]);
  transactions = computed(() => this.transactionsSignal());

  private totalReceivedSignal = signal<number>(0);
  totalReceivedLast30 = computed(() => this.totalReceivedSignal());

  private totalSentSignal = signal<number>(0);
  totalSentLast30 = computed(() => this.totalSentSignal());

  // ---- Personal ----
  favoriteUsers: UserRecipient[] = [];
  private userMap = new Map<number, string>(); // userId → full name

  // ---- Send Money Modal ----
  showSendModal = false;
  sendModalStep: 1 | 2 = 1;   // step 1 = amount+note, step 2 = PIN
  selectedSendUser: UserRecipient | null = null;
  sendAmount: number | null = null;
  sendNote = '';
  sendPin = '';
  sendLoading = false;
  sendError = '';
  sendSuccess = '';
  showSendPin = false;

  // ---- Business Stats (placeholders until dedicated services exist) ----
  pendingInvoices = 0;
  totalInvoiceAmount = 0;
  activeLoanCount = 0;
  totalRevenue30Days = 0;

  constructor(
    private walletService: WalletService,
    private transactionService: TransactionService,
    private transferService: TransferService,
    private profileService: ProfileService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.loadUserFromToken();
    this.loadProfile();
    this.loadWalletBalance();
    // transactions are loaded inside loadUsers() to ensure the name map is ready
  }

  private loadProfile(): void {
    this.profileService.getProfile().subscribe({
      next: res => {
        this.ngZone.run(() => {
          if (res.fullName) {
            this.userName = res.fullName;
            // Store so sidebar and other components can use it
            localStorage.setItem('userName', res.fullName);
          }
          this.accountType = res.accountType || '';

          // Persist to localStorage so sidebar can detect it on any page
          if (res.accountType) {
            localStorage.setItem('accountType', res.accountType);
          }

          // Add self to the user map
          const myId = Number(localStorage.getItem('userId'));
          if (myId && res.fullName) this.userMap.set(myId, res.fullName);

          this.loadUsers(); // both personal and business need user list for Send Money
          this.cdr.detectChanges();
        });
      }
    });
  }

  private loadUsers(): void {
    const colors = ['#4e73df', '#1cc88a', '#36b9cc', '#f6c23e', '#e74a3b', '#858796'];
    this.profileService.getAllUsers().subscribe({
      next: res => {
        this.ngZone.run(() => {
          this.favoriteUsers = res.map((u: any, i: number) => ({
            id: u.id,
            name: u.fullName || u.name || u.email || `User ${u.id}`,
            email: u.email,
            avatarColor: colors[i % colors.length]
          }));
          // Build the lookup map
          this.userMap.clear();
          res.forEach((u: any) => {
            this.userMap.set(u.id, u.fullName || u.name || u.email || `User ${u.id}`);
          });
          this.cdr.detectChanges();
          // Now load transactions — map is ready so names will resolve
          this.loadTransactions();
        });
      },
      error: () => {
        // Even if users fail, still show transactions
        this.ngZone.run(() => this.loadTransactions());
      }
    });
  }

  // ---- Token ----
  private loadUserFromToken(): void {
    const token = localStorage.getItem('token');
    if (!token) return;
    try {
      const payloadPart = token.split('.')[1];
      const decoded = JSON.parse(atob(payloadPart));
      if (decoded.sub) {
        this.userEmail = decoded.sub;
        if (this.userName === 'User') this.userName = decoded.sub;
      }
    } catch {
      // ignore decode errors
    }
  }

  // ---- Wallet ----
  private loadWalletBalance(): void {
    this.walletService.getBalance().subscribe({
      next: res => {
        this.ngZone.run(() => {
          this.walletBalanceSignal.set(res.balance);
          this.cdr.detectChanges();
        });
      }
    });
  }

  // ---- Transactions ----
  private loadTransactions(): void {
    this.transactionService.getMyTransactions().subscribe({
      next: res => {
        this.ngZone.run(() => {
          const currentUserId = Number(localStorage.getItem('userId')) || 0;
          const mapped: UiTransaction[] = res.map((tx: TransactionDto) => ({
            id: `#TXN-${tx.id}`,
            date: new Date(tx.createdAt).toLocaleString(),
            description: this.buildDescription(tx, currentUserId),
            amount: this.getAmount(tx, currentUserId),
            type: this.getType(tx, currentUserId),
            createdAt: tx.createdAt
          }));
          mapped.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
          this.transactionsSignal.set(mapped);
          this.computeTotals();
          this.cdr.detectChanges();
        });
      }
    });
  }

  private getAmount(tx: TransactionDto, uid: number): number {
    if (tx.type === 'TRANSFER' && tx.senderUserId === uid) return -tx.amount;
    if (tx.type === 'WITHDRAW') return -tx.amount;
    return tx.amount;
  }

  private getType(tx: TransactionDto, uid: number): 'debit' | 'credit' {
    if (tx.type === 'TRANSFER' && tx.senderUserId === uid) return 'debit';
    if (tx.type === 'WITHDRAW') return 'debit';
    return 'credit';
  }

  private buildDescription(tx: TransactionDto, uid: number): string {
    const resolveName = (userId: number | null | undefined): string => {
      if (!userId) return 'Unknown';
      return this.userMap.get(userId) || `User ${userId}`;
    };

    if (tx.type === 'TRANSFER') {
      return tx.senderUserId === uid
        ? `Transfer to ${resolveName(tx.receiverUserId)}`
        : `Transfer from ${resolveName(tx.senderUserId)}`;
    }
    if (tx.type === 'ADD_MONEY') return 'Added money to wallet';
    if (tx.type === 'WITHDRAW') return 'Withdrawn to bank';
    if (tx.type === 'REQUEST_ACCEPTED') {
      return tx.senderUserId === uid
        ? `Paid request to ${resolveName(tx.receiverUserId)}`
        : `Received payment from ${resolveName(tx.senderUserId)}`;
    }
    return tx.description || 'Transaction';
  }

  private computeTotals(): void {
    const thirtyDaysAgo = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000);
    let received = 0;
    let sent = 0;
    for (const tx of this.transactionsSignal()) {
      if (new Date(tx.createdAt) < thirtyDaysAgo) continue;
      if (tx.amount > 0) received += tx.amount;
      else sent += -tx.amount;
    }
    this.totalReceivedSignal.set(received);
    this.totalSentSignal.set(sent);
    // Business revenue = total received in last 30 days
    this.totalRevenue30Days = received;
  }

  // ---- Formatters ----
  private fmt(val: number): string {
    return new Intl.NumberFormat('en-IN', { style: 'currency', currency: this.walletCurrency }).format(val);
  }
  get formattedBalance(): string { return this.fmt(this.walletBalance()); }
  get formattedTotalReceived(): string { return this.fmt(this.totalReceivedLast30()); }
  get formattedTotalSent(): string { return this.fmt(this.totalSentLast30()); }
  get formattedRevenue(): string { return this.fmt(this.totalRevenue30Days); }

  // ---- Helpers ----
  getInitials(name: string): string {
    return (name || 'U').split(' ').map(p => p[0]).join('').toUpperCase();
  }

  onSendMoney(user: UserRecipient): void {
    this.selectedSendUser = user;
    this.sendAmount = null;
    this.sendNote = '';
    this.sendPin = '';
    this.sendError = '';
    this.sendSuccess = '';
    this.sendModalStep = 1;
    this.showSendPin = false;
    this.showSendModal = true;
  }

  closeSendModal(): void {
    this.showSendModal = false;
    this.selectedSendUser = null;
    this.sendLoading = false;
    this.sendError = '';
    this.sendSuccess = '';
  }

  proceedToPin(): void {
    if (!this.sendAmount || this.sendAmount <= 0) {
      this.sendError = 'Please enter a valid amount.';
      return;
    }
    if (this.sendAmount > this.walletBalance()) {
      this.sendError = 'Insufficient balance.';
      return;
    }
    this.sendError = '';
    this.sendModalStep = 2;
  }

  confirmSend(): void {
    if (!this.sendPin || this.sendPin.length < 4) {
      this.sendError = 'Enter your 4-digit transaction PIN.';
      return;
    }
    if (!this.selectedSendUser) return;
    this.sendLoading = true;
    this.sendError = '';

    const payload = {
      to: this.selectedSendUser.email,
      amount: this.sendAmount as number,
      note: this.sendNote,
      pin: this.sendPin
    };

    this.transferService.send(payload).subscribe({
      next: () => {
        this.ngZone.run(() => {
          this.sendLoading = false;
          this.sendSuccess = `₹${this.sendAmount} sent to ${this.selectedSendUser!.name} successfully!`;
          this.cdr.detectChanges();
          setTimeout(() => {
            this.closeSendModal();
            this.loadWalletBalance();
            this.loadTransactions();
          }, 1500);
        });
      },
      error: err => {
        this.ngZone.run(() => {
          this.sendLoading = false;
          this.sendError = err?.error?.message || err?.error || 'Transfer failed. Check your PIN and balance.';
          this.cdr.detectChanges();
        });
      }
    });
  }
}