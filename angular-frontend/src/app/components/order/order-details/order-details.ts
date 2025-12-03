import { CommonModule, Location } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { Order } from '../../../classes/order';
import { OrderItem } from '../../../classes/order-item';
import { OrderService } from '../../../services/order-service';
import { OrderAddress } from '../../../classes/order-address';
import { ActivatedRoute } from '@angular/router';
import { CustomerService } from '../../../services/customer-service';
import { User } from '../../../classes/user';
import { Customer } from '../../../classes/customer';
import { UserService } from '../../../services/user-service';

@Component({
  selector: 'app-order-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-details.html',
  styleUrl: './order-details.css',
})
export class OrderDetailsComponent implements OnInit {

  private order: Order = new Order;
  private orderedItems: OrderItem[] = [];
  private allAddresses: OrderAddress[] = []
  private orderService = inject(OrderService);
  private route = inject(ActivatedRoute);
  private allCustomers: Customer[] = [];
  private allUsers: User[] = [];
  private userService = inject(UserService);
  private customerService = inject(CustomerService);
  private location = inject(Location);

  ngOnInit(): void {
    Promise.all([
      this.getOrderDetails(this.route.snapshot.params[`orderId`]),
      this.listAllUsers(),
      this.listAllCustomers(),
      this.listAllAddresses(),
      this.listAllItems(this.route.snapshot.params[`orderId`])
    ])

      .catch((error) => {
        console.log(`Error loading functions  ${error}`);
      })
  }

  getOrder(): Order {
    return this.order;
  }

  getOrderedItems(): OrderItem[] {
    return this.orderedItems;
  }

  getCustomer(): Customer {
    var returnValue: Customer = new Customer;
    this.allCustomers.forEach((tempCustomer) => {
      if (tempCustomer.cartId === this.order.cartId) {
        returnValue = tempCustomer;
      }
    })

    return returnValue;
  }

  getUser(): User {
    var returnValue: User = new User;
    var customer = this.getCustomer();
    this.allUsers.forEach((tempUser) => {
      if (tempUser.id === customer.userId) {
        returnValue = tempUser;
      }
    })

    return returnValue;
  }

  getAddress(): OrderAddress {
    var returnValue: OrderAddress = new OrderAddress;
    this.allAddresses.forEach((address) => {
      if (address.orderAddressId === this.order.addressId) {
        returnValue = address;
      }
    })

    return returnValue;
  }

  getOrderDetails(orderId: any) {
    this.orderService.getOrderDetails(orderId)
      .then((response) => {
        this.order = response.data;
      });
  }

  listAllUsers() {
    this.userService.collectAllUsers()
      .then((response) => {
        this.allUsers = response.data;
      });
  }

  listAllCustomers() {
    this.customerService.collectAllCustomers()
      .then((response) => {
        this.allCustomers = response.data;
      });
  }

  listAllAddresses() {
    this.orderService.collectAllAddresses()
      .then((response) => {
        this.allAddresses = response.data;
      });
  }

  listAllItems(orderId: any) {
    this.orderService.collectAllItems(orderId)
      .then((response) => {
        this.orderedItems = response.data;
      });
  }

  getOrderTotal() {
    var returnValue: number = 0;
    this.orderedItems.forEach((item) => {
      if (item.price) {
        returnValue = returnValue + item.price;
      }
    })

    return returnValue;
  }

  deleteOrder(orderId: any) {
    if (confirm(`Remove this order?`)) {
      this.orderService.deleteOrder(orderId)
        .then(() => {
          this.orderService.redirectAllOrders();
        })

        .catch((error) => {
          console.log(error);
          alert(`Failed!`);
        })
    }

  }

  redirectAllOrders() {
    this.orderService.redirectAllOrders();
  }

  goBack() {
    this.location.back();
  }

}
