import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import axios from 'axios';



@Injectable({
  providedIn: 'root'
})
export class ProductCategoryService {

  private router = inject(Router);
  private targetUrl = `http://localhost:8080/api/categories`;

  redirectAllCategories() {
    this.router.navigate([`categories`]);
  }

  collectAllCategories(): Promise<any> {
    return axios.get(`${this.targetUrl}`);
  }

  deleteCategory(categoryId: any): Promise<any> {
    return axios.delete(`${this.targetUrl}/${categoryId}`);
  }

  getCategoryDetails(categoryId: any): Promise<any> {
    return axios.get(`${this.targetUrl}/${categoryId}`);
  }

  public getTargetUrl(): string {
    return this.targetUrl;
  }



}