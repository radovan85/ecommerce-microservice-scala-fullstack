import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import axios from 'axios';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private router = inject(Router);
  private targetUrl = `http://localhost:8080/api/products`;

  redirectAllProducts() {
    this.router.navigate([`products`]);
  }

  collectAllProducts(): Promise<any> {
    return axios.get(`${this.targetUrl}`);
  }

  getProductDetails(productId: any): Promise<any> {
    return axios.get(`${this.targetUrl}/${productId}`);
  }

  deleteProduct(productId: any): Promise<any> {
    return axios.delete(`${this.targetUrl}/${productId}`);
  }

  collectAllImages(): Promise<any> {
    return axios.get(`${this.targetUrl}/getAllImages`);
  }

  getTargetUrl() {
    return this.targetUrl;
  }

}