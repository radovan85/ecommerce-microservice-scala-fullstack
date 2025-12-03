import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ValidationService {

  validateProductCategory() {
    var categoryName = (<HTMLInputElement>document.getElementById('categoryName')).value.trim();
    var categoryNameError = document.getElementById('categoryNameError');
    var returnValue = true;

    if (categoryNameError) {
      if (categoryName === '') {
        categoryNameError.innerHTML = 'Category name cannot be empty.';
        categoryNameError.style.visibility = 'visible';
        returnValue = false;
      } else if (categoryName.length > 40) {
        categoryNameError.innerHTML = 'Maximum 40 characters allowed.';
        categoryNameError.style.visibility = 'visible';
        returnValue = false;
      } else {
        categoryNameError.innerHTML = '';
        categoryNameError.style.visibility = 'hidden';
      }
    }

    return returnValue;
  }


  validateProduct() {
    var productName = (<HTMLInputElement>document.getElementById(`product-name`)).value.trim();
    var description = (<HTMLInputElement>document.getElementById(`product-description`)).value.trim();
    var productBrand = (<HTMLInputElement>document.getElementById(`product-brand`)).value.trim();
    var productModel = (<HTMLInputElement>document.getElementById(`product-model`)).value.trim();
    var productPrice = (<HTMLInputElement>document.getElementById(`product-price`)).value.trim();
    var unitStock = (<HTMLInputElement>document.getElementById(`unit-stock`)).value.trim();
    var discount = (<HTMLInputElement>document.getElementById(`discount`)).value.trim();
    var categoryId = (<HTMLInputElement>document.getElementById(`categoryId`)).value.trim();

    var nameError = document.getElementById(`nameError`);
    var descriptionError = document.getElementById(`descriptionError`);
    var brandError = document.getElementById(`brandError`);
    var modelError = document.getElementById(`modelError`);
    var priceError = document.getElementById(`priceError`);
    var stockError = document.getElementById(`stockError`);
    var discountError = document.getElementById(`discountError`);
    var categoryIdError = document.getElementById(`categoryIdError`);

    var productPriceNumber = Number(productPrice);
    var unitStockNumber = Number(unitStock);
    var discountNumber = Number(discount);
    var returnValue = true;

    if (nameError) {
      if (productName === `` || productName.length > 40) {
        returnValue = false;
        nameError.style.visibility = `visible`;
        nameError.innerHTML = `Name is required and max 40 characters allowed!`;
      } else {
        nameError.style.visibility = `hidden`;
        nameError.innerHTML = ``;
      }
    }

    if (descriptionError) {
      if (description === `` || description.length > 100) {
        returnValue = false;
        descriptionError.style.visibility = `visible`;
        descriptionError.innerHTML = `Description is required and max 100 characters allowed!`;
      } else {
        descriptionError.style.visibility = `hidden`;
        descriptionError.innerHTML = ``;
      }
    }

    if (brandError) {
      if (productBrand === `` || productBrand.length > 40) {
        returnValue = false;
        brandError.style.visibility = `visible`;
        brandError.innerHTML = `Brand is required and max 40 characters allowed!`;
      } else {
        brandError.style.visibility = `hidden`;
        brandError.innerHTML = ``;
      }
    }

    if (modelError) {
      if (productModel === `` || productModel.length > 40) {
        returnValue = false;
        modelError.style.visibility = `visible`;
        modelError.innerHTML = `Model is required and max 40 characters allowed!`;
      } else {
        modelError.style.visibility = `hidden`;
        modelError.innerHTML = ``;
      }
    }

    if (priceError) {
      if (productPrice === `` || productPriceNumber < 1) {
        returnValue = false;
        priceError.style.visibility = `visible`;
        priceError.innerHTML = `Price must be a positive number!`;
      } else {
        priceError.style.visibility = `hidden`;
        priceError.innerHTML = ``;
      }
    }

    if (stockError) {
      if (unitStock === `` || unitStockNumber < 0) {
        returnValue = false;
        stockError.style.visibility = `visible`;
        stockError.innerHTML = `Stock must be zero or a positive number!`;
      } else {
        stockError.style.visibility = `hidden`;
        stockError.innerHTML = ``;
      }
    }

    if (discountError) {
      if (discount === `` || discountNumber < 0) {
        returnValue = false;
        discountError.style.visibility = `visible`;
        discountError.innerHTML = `Discount must be zero or a positive number!`;
      } else {
        discountError.style.visibility = `hidden`;
        discountError.innerHTML = ``;
      }
    }

    if (categoryIdError) {
      if (categoryId === ``) {
        returnValue = false;
        categoryIdError.style.visibility = `visible`;
        categoryIdError.innerHTML = `Category is required!`;
      } else {
        categoryIdError.style.visibility = `hidden`;
        categoryIdError.innerHTML = ``;
      }
    }

    return returnValue;
  }


  validateNumber(event: KeyboardEvent): void {
    var allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Tab'];

    if (!/^[\d.]$/.test(event.key) && !allowedKeys.includes(event.key)) {
      event.preventDefault();
    }
  }


  validateRegForm() {
    var firstName = (<HTMLInputElement>document.getElementById('firstName')).value.trim();
    var lastName = (<HTMLInputElement>document.getElementById('lastName')).value.trim();
    var email = (<HTMLInputElement>document.getElementById('email')).value.trim();
    var password = (<HTMLInputElement>document.getElementById('password')).value.trim();
    var confirmPassword = (<HTMLInputElement>document.getElementById('confirmPassword')).value.trim();
    var phone = (<HTMLInputElement>document.getElementById('phone')).value.trim();
    var address = (<HTMLInputElement>document.getElementById('address')).value.trim();
    var city = (<HTMLInputElement>document.getElementById('city')).value.trim();
    var state = (<HTMLInputElement>document.getElementById('state')).value.trim();
    var country = (<HTMLInputElement>document.getElementById('country')).value.trim();
    var postcode = (<HTMLInputElement>document.getElementById('postcode')).value.trim();

    var firstNameError = document.getElementById('firstNameError');
    var lastNameError = document.getElementById('lastNameError');
    var emailError = document.getElementById('emailError');
    var passwordError = document.getElementById('passwordError');
    var phoneError = document.getElementById('phoneError');
    var addressError = document.getElementById('addressError');
    var cityError = document.getElementById('cityError');
    var stateError = document.getElementById('stateError');
    var countryError = document.getElementById('countryError');
    var postcodeError = document.getElementById('postcodeError');

    var regEmail = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/g;
    var returnValue = true;

    // Password match check
    if (password !== confirmPassword) {
      if (passwordError) {
        passwordError.innerHTML = `Passwords do not match.`;
        passwordError.style.visibility = 'visible';
      }
      returnValue = false;
    }

    if (firstNameError) {
      if (firstName === '') {
        firstNameError.innerHTML = `First name is required.`;
        firstNameError.style.visibility = 'visible';
        returnValue = false;
      } else if (firstName.length > 30) {
        firstNameError.innerHTML = `First name must be under 30 characters.`;
        firstNameError.style.visibility = 'visible';
        returnValue = false;
      } else {
        firstNameError.innerHTML = '';
        firstNameError.style.visibility = 'hidden';
      }
    }

    if (lastNameError) {
      if (lastName === '') {
        lastNameError.innerHTML = `Last name is required.`;
        lastNameError.style.visibility = 'visible';
        returnValue = false;
      } else if (lastName.length > 30) {
        lastNameError.innerHTML = `Last name must be under 30 characters.`;
        lastNameError.style.visibility = 'visible';
        returnValue = false;
      } else {
        lastNameError.innerHTML = '';
        lastNameError.style.visibility = 'hidden';
      }
    }

    if (emailError) {
      if (email === '') {
        emailError.innerHTML = `Email is required.`;
        emailError.style.visibility = 'visible';
        returnValue = false;
      } else if (email.length > 50) {
        emailError.innerHTML = `Email must be under 50 characters.`;
        emailError.style.visibility = 'visible';
        returnValue = false;
      } else if (!regEmail.test(email)) {
        emailError.innerHTML = `Invalid email format.`;
        emailError.style.visibility = 'visible';
        returnValue = false;
      } else {
        emailError.innerHTML = '';
        emailError.style.visibility = 'hidden';
      }
    }

    if (passwordError) {
      if (password.length < 6) {
        passwordError.innerHTML = `Password must be at least 6 characters.`;
        passwordError.style.visibility = 'visible';
        returnValue = false;
      } else if (password.length > 30) {
        passwordError.innerHTML = `Password must be under 30 characters.`;
        passwordError.style.visibility = 'visible';
        returnValue = false;
      } else if (password === confirmPassword) {
        passwordError.innerHTML = '';
        passwordError.style.visibility = 'hidden';
      }
    }

    if (phoneError) {
      if (phone.length < 9 || phone.length > 15) {
        phoneError.innerHTML = `Phone number must be between 9 and 15 digits.`;
        phoneError.style.visibility = 'visible';
        returnValue = false;
      } else {
        phoneError.innerHTML = '';
        phoneError.style.visibility = 'hidden';
      }
    }

    if (addressError) {
      if (address === '') {
        addressError.innerHTML = `Address is required.`;
        addressError.style.visibility = 'visible';
        returnValue = false;
      } else if (address.length > 75) {
        addressError.innerHTML = `Address must be under 75 characters.`;
        addressError.style.visibility = 'visible';
        returnValue = false;
      } else {
        addressError.innerHTML = '';
        addressError.style.visibility = 'hidden';
      }
    }

    if (cityError) {
      if (city === '') {
        cityError.innerHTML = `City is required.`;
        cityError.style.visibility = 'visible';
        returnValue = false;
      } else if (city.length > 40) {
        cityError.innerHTML = `City must be under 40 characters.`;
        cityError.style.visibility = 'visible';
        returnValue = false;
      } else {
        cityError.innerHTML = '';
        cityError.style.visibility = 'hidden';
      }
    }

    if (stateError) {
      if (state === '') {
        stateError.innerHTML = `State is required.`;
        stateError.style.visibility = 'visible';
        returnValue = false;
      } else if (state.length > 40) {
        stateError.innerHTML = `State must be under 40 characters.`;
        stateError.style.visibility = 'visible';
        returnValue = false;
      } else {
        stateError.innerHTML = '';
        stateError.style.visibility = 'hidden';
      }
    }

    if (countryError) {
      if (country === '') {
        countryError.innerHTML = `Country is required.`;
        countryError.style.visibility = 'visible';
        returnValue = false;
      } else if (country.length > 40) {
        countryError.innerHTML = `Country must be under 40 characters.`;
        countryError.style.visibility = 'visible';
        returnValue = false;
      } else {
        countryError.innerHTML = '';
        countryError.style.visibility = 'hidden';
      }
    }

    if (postcodeError) {
      if (postcode.length < 5 || postcode.length > 10) {
        postcodeError.innerHTML = `Postcode must be between 5 and 10 characters.`;
        postcodeError.style.visibility = 'visible';
        returnValue = false;
      } else {
        postcodeError.innerHTML = '';
        postcodeError.style.visibility = 'hidden';
      }
    }

    return returnValue;
  }


  validateShippingAddress() {

    var address = (<HTMLInputElement>document.getElementById('address')).value.trim();
    var city = (<HTMLInputElement>document.getElementById('city')).value.trim();
    var state = (<HTMLInputElement>document.getElementById('state')).value.trim();
    var country = (<HTMLInputElement>document.getElementById('country')).value.trim();
    var postcode = (<HTMLInputElement>document.getElementById('postcode')).value.trim();
  
    var addressError = document.getElementById('addressError');
    var cityError = document.getElementById('cityError');
    var stateError = document.getElementById('stateError');
    var countryError = document.getElementById('countryError');
    var postcodeError = document.getElementById('postcodeError');
  
    var returnValue = true;
  
    if (addressError) {
      if (address === '') {
        addressError.innerHTML = `Address is required.`;
        addressError.style.visibility = 'visible';
        returnValue = false;
      } else if (address.length > 75) {
        addressError.innerHTML = `Address must be under 75 characters.`;
        addressError.style.visibility = 'visible';
        returnValue = false;
      } else {
        addressError.innerHTML = '';
        addressError.style.visibility = 'hidden';
      }
    }
  
    if (cityError) {
      if (city === '') {
        cityError.innerHTML = `City is required.`;
        cityError.style.visibility = 'visible';
        returnValue = false;
      } else if (city.length > 40) {
        cityError.innerHTML = `City must be under 40 characters.`;
        cityError.style.visibility = 'visible';
        returnValue = false;
      } else {
        cityError.innerHTML = '';
        cityError.style.visibility = 'hidden';
      }
    }
  
    if (stateError) {
      if (state === '') {
        stateError.innerHTML = `State is required.`;
        stateError.style.visibility = 'visible';
        returnValue = false;
      } else if (state.length > 40) {
        stateError.innerHTML = `State must be under 40 characters.`;
        stateError.style.visibility = 'visible';
        returnValue = false;
      } else {
        stateError.innerHTML = '';
        stateError.style.visibility = 'hidden';
      }
    }
  
    if (countryError) {
      if (country === '') {
        countryError.innerHTML = `Country is required.`;
        countryError.style.visibility = 'visible';
        returnValue = false;
      } else if (country.length > 40) {
        countryError.innerHTML = `Country must be under 40 characters.`;
        countryError.style.visibility = 'visible';
        returnValue = false;
      } else {
        countryError.innerHTML = '';
        countryError.style.visibility = 'hidden';
      }
    }
  
    if (postcodeError) {
      if (postcode.length < 5) {
        postcodeError.innerHTML = `Postcode must be at least 5 characters.`;
        postcodeError.style.visibility = 'visible';
        returnValue = false;
      } else if (postcode.length > 10) {
        postcodeError.innerHTML = `Postcode must be under 10 characters.`;
        postcodeError.style.visibility = 'visible';
        returnValue = false;
      } else {
        postcodeError.innerHTML = '';
        postcodeError.style.visibility = 'hidden';
      }
    }
  
    return returnValue;
  }
  


  validateCustomerPhone() {
    var customerPhone = (<HTMLInputElement>document.getElementById('customerPhone')).value.trim();
    var customerPhoneError = document.getElementById('customerPhoneError');
    var returnValue = true;
  
    if (customerPhoneError) {
      if (customerPhone === '') {
        customerPhoneError.innerHTML = `Phone number is required.`;
        customerPhoneError.style.visibility = 'visible';
        returnValue = false;
      } else if (customerPhone.length < 9) {
        customerPhoneError.innerHTML = `Phone number must be at least 9 digits.`;
        customerPhoneError.style.visibility = 'visible';
        returnValue = false;
      } else if (customerPhone.length > 15) {
        customerPhoneError.innerHTML = `Phone number must be under 15 digits.`;
        customerPhoneError.style.visibility = 'visible';
        returnValue = false;
      } else {
        customerPhoneError.innerHTML = '';
        customerPhoneError.style.visibility = 'hidden';
      }
    }
  
    return returnValue;
  }
  

}