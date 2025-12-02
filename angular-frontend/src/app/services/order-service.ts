import { ThisReceiver } from '@angular/compiler';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import axios from 'axios';

@Injectable({
  providedIn: 'root'
})
export class OrderService {

  private router = inject(Router);
  private targetUrl = `http://localhost:8080/api/order`

  redirectAddressConfirm() {
    this.router.navigate([`order/addressConfirmation`]);
  }

  provideMyAddress(): Promise<any> {
    return axios.get(`${this.targetUrl}/provideMyAddress`);
  }

  cancelOrder() {
    this.router.navigate([`order/cancelled`]);
  }

  redirectPhoneConfirmation() {
    this.router.navigate([`order/phoneConfirmation`]);
  }

  redirectOrderConfirmation() {
    this.router.navigate([`order/orderConfirmation`]);
  }

  redirectOrderCompleted() {
    this.router.navigate([`order/completed`]);
  }

  placeOrder(): Promise<any> {
    return axios.post(`${this.targetUrl}`);
  }

  redirectAllOrders() {
    this.router.navigate([`order/orderList`]);
  }

  collectAllOrders(): Promise<any> {
    return axios.get(`${this.targetUrl}`);
  }

  redirectOrderDetails(orderId: any) {
    this.router.navigate([`order/orderDetails/${orderId}`]);
  }

  getOrderDetails(orderId: any): Promise<any> {
    return axios.get(`${this.targetUrl}/${orderId}`);
  }

  collectAllAddresses(): Promise<any> {
    return axios.get(`${this.targetUrl}/allAddresses`);
  }

  collectAllItems(orderId: any): Promise<any> {
    return axios.get(`${this.targetUrl}/allItems/${orderId}`);
  }

  deleteOrder(orderId: any): Promise<any> {
    return axios.delete(`${this.targetUrl}/${orderId}`);
  }

  getTargetUrl() {
    return this.targetUrl;
  }

}