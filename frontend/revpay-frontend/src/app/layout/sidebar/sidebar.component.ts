import { Component, Input, EventEmitter, Output, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ProfileService } from '../../core/services/profile.service';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.scss']
})
export class SidebarComponent implements OnInit {

  @Input() isOpen: boolean = true;
  @Output() toggleSidebarEvent = new EventEmitter<void>();

  showProfileMenu = false;
  role: string | null = null;

  // Real user info for the sidebar footer
  displayName = 'User';
  displayEmail = '';
  avatarInitials = 'U';

  constructor(
    private auth: AuthService,
    private router: Router,
    private profileService: ProfileService,
    private cdr: ChangeDetectorRef,
    private ngZone: NgZone
  ) {}

  ngOnInit(): void {
    this.role = this.auth.getRole();
    this.loadProfile();
  }

  private loadProfile(): void {
    this.profileService.getProfile().subscribe({
      next: res => {
        this.ngZone.run(() => {
          if (res?.fullName) {
            this.displayName = res.fullName;
            this.avatarInitials = res.fullName
              .split(' ')
              .map((p: string) => p[0])
              .join('')
              .toUpperCase()
              .slice(0, 2);
          }
          if (res?.email) this.displayEmail = res.email;
          if (res?.accountType) localStorage.setItem('accountType', res.accountType);
          this.cdr.detectChanges();
        });
      }
    });
  }

  get isBusiness(): boolean {
    return this.role === 'ROLE_BUSINESS' ||
           localStorage.getItem('accountType') === 'BUSINESS';
  }

  toggleProfileMenu() {
    this.showProfileMenu = !this.showProfileMenu;
  }

  goToProfile() {
    this.showProfileMenu = false;
    this.router.navigate(['/profile']);
  }

  logout() {
    this.showProfileMenu = false;
    localStorage.removeItem('accountType');
    this.auth.logout();
    this.router.navigate(['/auth/login']);
  }

  onToggleSidebar() {
    this.toggleSidebarEvent.emit();
  }
}