import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { OrderService } from '../../../services/order-service';
import { ValidationService } from '../../../services/validation-service';
import { CustomerService } from '../../../services/customer-service';
import { Customer } from '../../../classes/customer';
import axios from 'axios';

@Component({
  selector: 'app-phone-confirmation',
  imports: [RouterLink],
  standalone: true,
  templateUrl: './phone-confirmation.html',
  styleUrl: './phone-confirmation.css',
})
export class PhoneConfirmationComponent implements OnInit, AfterViewInit {

  private orderService = inject(OrderService);
  private customerService = inject(CustomerService);
  private customer: Customer = new Customer;
  private validationService = inject(ValidationService);

  ngOnInit(): void {
    this.getCurrentCustomer();
  }

  ngAfterViewInit(): void {
    this.executeCustomerForm();
  }

  redirectAddressConfirmation() {
    this.orderService.redirectAddressConfirm();
  }

  getCurrentCustomer(): Promise<any> {
    return new Promise(() => {
      this.customerService.getCurrentCustomer()
        .then((response) => {
          this.customer = response.data;
        })
    })
  }

  getCustomer(): Customer {
    return this.customer;
  }

  executeCustomerForm() {
    var form = document.getElementById(`customerForm`) as HTMLFormElement;
    form.addEventListener(`submit`, async (event) => {
      event.preventDefault();

      var formData = new FormData(form);
      var serializedData: { [key: string]: string } = {};
      formData.forEach((value, key) => {
        serializedData[key] = value.toString().trim();
      });

      if (this.validationService.validateCustomerPhone()) {
        axios.put(`${this.customerService.getTargetUrl()}`, {
          customerPhone: serializedData[`customerPhone`]
        })

          .then(() => {
            this.orderService.redirectOrderConfirmation();
          })

          .catch((error) => {
            console.log(error);
            alert(`Failed!`);
          })
      }

    })
  }

}
