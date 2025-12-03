import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import axios from 'axios';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private targetUrl = `http://localhost:8080/api/customers`;
  private router = inject(Router);

  getCurrentCustomer(): Promise<any> {
    return axios.get(`${this.targetUrl}/getCurrentCustomer`);
  }

  redirectAllCustomers() {
    this.router.navigate([`customers`]);
  }

  collectAllCustomers(): Promise<any> {
    return axios.get(`${this.targetUrl}`);
  }

  getCustomerDetails(customerId: any): Promise<any> {
    return axios.get(`${this.targetUrl}/${customerId}`);
  }

  deleteCustomer(customerId: any): Promise<any> {
    return axios.delete(`${this.targetUrl}/${customerId}`);
  }

  suspendCustomer(customerId: any): Promise<any> {
    return axios.put(`${this.targetUrl}/suspend/${customerId}`);
  }

  reactivateCustomer(customerId: any): Promise<any> {
    return axios.put(`${this.targetUrl}/reactivate/${customerId}`);
  }

  getTargetUrl() {
    return this.targetUrl;
  }
}