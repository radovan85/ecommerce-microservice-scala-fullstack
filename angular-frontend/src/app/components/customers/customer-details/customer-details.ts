import { Component, inject, OnInit } from '@angular/core';
import { CommonModule, Location } from '@angular/common';
import { Customer } from '../../../classes/customer';
import { ActivatedRoute } from '@angular/router';
import { CustomerService } from '../../../services/customer-service';
import { UserService } from '../../../services/user-service';
import { User } from '../../../classes/user';
import { ShippingAddressService } from '../../../services/shipping-address-service';
import { ShippingAddress } from '../../../classes/shipping-address';

@Component({
  selector: 'app-customer-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './customer-details.html',
  styleUrl: './customer-details.css',
})
export class CustomerDetailsComponent implements OnInit {

  private customer: Customer = new Customer;
  private route = inject(ActivatedRoute);
  private customerService = inject(CustomerService);
  private userService = inject(UserService);
  private allUsers: User[] = [];
  private addressService = inject(ShippingAddressService);
  private allAddresses: ShippingAddress[] = [];
  private location = inject(Location);

  ngOnInit(): void {
    Promise.all([
      this.getCustomerDetails(this.route.snapshot.params[`customerId`]),
      this.listAllUsers(),
      this.listAllAddresses()
    ])

      .catch((error) => {
        console.log(`Error loading the functions ${error}`);
      })
  }

  deleteCustomer(customerId: any) {
    if (confirm(`Are you sure you want to remove this customer?\nIt will affect all related data!`)) {
      this.customerService.deleteCustomer(customerId)

        .then(() => {
          this.customerService.redirectAllCustomers();
        })

        .catch((error) => {
          console.log(error);
          alert(`Failed!`);
        })
    }
  }

  getCustomerDetails(customerId: any) {
    this.customerService.getCustomerDetails(customerId)
      .then((response) => {
        this.customer = response.data;
      });
  }

  getCustomer(): Customer {
    return this.customer;
  }

  listAllUsers() {
    this.userService.collectAllUsers()
      .then((response) => {
        this.allUsers = response.data;
      });
  }

  getUserById(userId: any): User {
    var returnValue: User = new User;
    this.allUsers.forEach((user) => {
      if (user.id === userId) {
        returnValue = user;
      }
    })

    return returnValue;
  }

  listAllAddresses() {
    this.addressService.collectAllAddresses()
      .then((response) => {
        this.allAddresses = response.data;
      });
  }

  getAddress(addressId: any) {
    var returnValue: ShippingAddress = new ShippingAddress;
    this.allAddresses.forEach((address) => {
      if (address.shippingAddressId === addressId) {
        returnValue = address;
      }
    })

    return returnValue;
  }

  reactivateCustomer(customerId: any) {
    if (confirm(`Are you sure you want to reactivate this customer?`)) {
      this.customerService.reactivateCustomer(customerId)

        .then(() => {
          this.customerService.redirectAllCustomers();
        })

        .catch((error) => {
          console.log(error);
          alert(`Failed!`);
        })
    }
  }

  suspendCustomer(customerId: any) {
    if (confirm(`Are you sure you want to suspend this customer?`)) {
      this.customerService.suspendCustomer(customerId)
        .then(() => {
          this.customerService.redirectAllCustomers();
        })

        .catch((error) => {
          console.log(error);
          alert(`Failed!`);
        })
    }
  }

  goBack() {
    this.location.back();
  }

}