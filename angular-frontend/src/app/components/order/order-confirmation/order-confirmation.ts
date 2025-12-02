import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../services/order-service';
import { ShippingAddress } from '../../../classes/shipping-address';
import { CartItem } from '../../../classes/cart-item';
import { Product } from '../../../classes/product';
import { ProductService } from '../../../services/product-service';
import { CartService } from '../../../services/cart-service';
import { Cart } from '../../../classes/cart';

@Component({
  selector: 'app-order-confirmation',
  imports: [CommonModule, RouterLink],
  standalone: true,
  templateUrl: './order-confirmation.html',
  styleUrl: './order-confirmation.css',
})
export class OrderConfirmationComponent implements OnInit {

  private orderService = inject(OrderService);
  private address: ShippingAddress = new ShippingAddress;
  private allItems: CartItem[] = [];
  private allProducts: Product[] = [];
  private productService = new ProductService;
  private cartService = inject(CartService);
  private cart = new Cart;

  ngOnInit(): void {
    Promise.all([
      this.provideMyAddress(),
      this.listAllProducts(),
      this.listAllItems(),
      this.getMyCart()
    ])

      .catch((error) => {
        console.log(`Error loading functions! ${error}`);
      })

  }

  provideMyAddress() {
    this.orderService.provideMyAddress()
      .then((response) => {
        setTimeout(() => {
          this.address = response.data;
        })
      });
  }

  getAllItems(): CartItem[] {
    return this.allItems;
  }

  getAddress(): ShippingAddress {
    return this.address;
  }

  listAllProducts() {
    this.productService.collectAllProducts()
      .then((response) => {
        setTimeout(() => {
          this.allProducts = response.data;
        })
      });
  }

  getProduct(productId: any): Product {
    var tempProduct = new Product;
    this.allProducts.forEach((product) => {
      if (product.productId === productId) {
        tempProduct = product;
      }
    })

    return tempProduct;
  }



  listAllItems() {
    this.cartService.collectMyItems()
      .then((response) => {
        this.allItems = response.data;
      });
  }

  getMyCart() {
    this.cartService.getMyCart()
      .then((response) => {
        this.cart = response.data;
      });
  }

  getCart(): Cart {
    return this.cart;
  }

  redirectPhoneConfirmation() {
    this.orderService.redirectPhoneConfirmation();
  }

  placeOrder() {
    this.orderService.placeOrder()
      .then(() => {
        this.orderService.redirectOrderCompleted();
      })

      .catch((error) => {
        if (error.response.status === 406) {
          alert(error.response.data);
        }

        else {
          alert(`Failed!`);
        }
      })
  }

}
