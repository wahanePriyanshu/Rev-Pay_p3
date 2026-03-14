import { Component, OnInit } from '@angular/core';
import { CustomerService, CustomerDto } from '../../core/services/customer.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customers.html',
  styleUrl: './customers.scss',
})
export class Customers implements OnInit {
  customers: CustomerDto[] = [];
  filteredCustomers: CustomerDto[] = [];

  // Add form
  newCustomer: Partial<CustomerDto> = {};
  showAddForm = false;
  adding = false;

  // Edit modal
  editCustomer: Partial<CustomerDto> = {};
  showEditModal = false;
  editing = false;
  editingId: number | null = null;

  // Delete confirm
  deletingId: number | null = null;

  // UI state
  loading = false;
  searchTerm = '';
  error: string | null = null;

  constructor(
    private customerService: CustomerService,
    private toast: ToastService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers() {
    this.loading = true;
    this.customerService.getCustomers().subscribe({
      next: res => {
        this.customers = res;
        this.filteredCustomers = res;
        this.loading = false;
      },
      error: () => {
        this.error = 'Failed to load customers';
        this.loading = false;
      }
    });
  }

  applySearch() {
    const term = this.searchTerm.toLowerCase();
    this.filteredCustomers = this.customers.filter(c =>
      c.name.toLowerCase().includes(term) ||
      c.email.toLowerCase().includes(term) ||
      (c.address || '').toLowerCase().includes(term)
    );
  }

  getInitials(name: string): string {
    return name.split(' ').map(p => p[0]).join('').substring(0, 2).toUpperCase();
  }

  getAvatarColor(name: string): string {
    const colors = ['#4e73df','#1cc88a','#e74a3b','#f6c23e','#36b9cc','#858796','#5a5c69'];
    let hash = 0;
    for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
    return colors[Math.abs(hash) % colors.length];
  }

  // ===== Add Customer =====
  addCustomer() {
    if (!this.newCustomer.name || !this.newCustomer.email) {
      this.toast.show('Name and email are required', 'error');
      return;
    }
    this.adding = true;
    this.customerService.createCustomer(this.newCustomer as CustomerDto).subscribe({
      next: (res) => {
        this.customers.push(res);
        this.filteredCustomers = [...this.customers];
        this.newCustomer = {};
        this.showAddForm = false;
        this.adding = false;
        this.toast.show('Customer added successfully', 'success');
      },
      error: () => {
        this.adding = false;
        this.toast.show('Failed to create customer', 'error');
      }
    });
  }

  // ===== Edit Customer =====
  openEdit(c: CustomerDto) {
    this.editCustomer = { ...c };
    this.editingId = c.id;
    this.showEditModal = true;
  }

  saveEdit() {
    if (!this.editingId || !this.editCustomer.name || !this.editCustomer.email) {
      this.toast.show('Name and email are required', 'error');
      return;
    }
    this.editing = true;
    this.customerService.updateCustomer(this.editingId, this.editCustomer as CustomerDto).subscribe({
      next: (updated) => {
        const idx = this.customers.findIndex(c => c.id === this.editingId);
        if (idx !== -1) this.customers[idx] = updated;
        this.filteredCustomers = [...this.customers];
        this.showEditModal = false;
        this.editing = false;
        this.editingId = null;
        this.toast.show('Customer updated', 'success');
      },
      error: () => {
        this.editing = false;
        this.toast.show('Failed to update customer', 'error');
      }
    });
  }

  // ===== Delete Customer =====
  deleteCustomer(id: number) {
    this.deletingId = id;
    this.customerService.deleteCustomer(id).subscribe({
      next: () => {
        this.customers = this.customers.filter(c => c.id !== id);
        this.filteredCustomers = [...this.customers];
        this.deletingId = null;
        this.toast.show('Customer deleted', 'success');
      },
      error: () => {
        this.deletingId = null;
        this.toast.show('Failed to delete customer', 'error');
      }
    });
  }
}
