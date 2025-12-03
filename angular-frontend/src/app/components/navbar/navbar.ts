import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';

import { User } from '../../classes/user';
import { AuthService } from '../../services/auth-service';
import { UserService } from '../../services/user-service';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent implements OnInit {


  private isAuthenticated = false;
  private hasAuthorityAdmin = false;
  private hasAuthorityUser = false;
  private authUser: User = new User;
  private authService = inject(AuthService);
  private userService = inject(UserService);


  ngOnInit(): void {
    Promise.all([
      this.hasAuthorityAdmin = this.authService.isAdmin(),
      this.isAuthenticated = this.authService.isAuthenticated(),
      this.hasAuthorityUser = this.authService.isUser(),
      this.getCurrentUser()
    ])
  }

  logout() {
    this.authService.logout();
  }

  getCurrentUser() {
    if (this.authService.isAuthenticated()) {
      this.userService.getCurrentUser()
        .then((response) => {
          setTimeout(() => {
            this.authUser = response.data;
          })
        })
    }
  }

  public getIsAuthenticated(): boolean {
    return this.isAuthenticated;
  }

  public getHasAuthorityAdmin(): boolean {
    return this.hasAuthorityAdmin;
  }

  public getHasAuthorityUser(): boolean {
    return this.hasAuthorityUser;
  }

  public getAuthUser(): User {
    return this.authUser;
  }




}