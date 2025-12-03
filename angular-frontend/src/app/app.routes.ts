import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { authGuard } from './guards/auth-guard';
import { unidentifiedGuard } from './guards/unidentified-guard';
import { HomeComponent } from './components/home/home';
import { adminGuard } from './guards/admin-guard';
import { CategoryListComponent } from './components/productCategories/category-list/category-list';
import { CategoryFormComponent } from './components/productCategories/category-form/category-form';
import { CategoryUpdateFormComponent } from './components/productCategories/category-update-form/category-update-form';
import { ProductListComponent } from './components/products/product-list/product-list';
import { ProductFormComponent } from './components/products/product-form/product-form';
import { ProductUpdateFormComponent } from './components/products/product-update-form/product-update-form';
import { ProductDetailsComponent } from './components/products/product-details/product-details';
import { RegistrationFormComponent } from './components/registration/registration-form/registration-form';
import { userGuard } from './guards/user-guard';
import { CartComponent } from './components/cart/cart/cart';
import { InvalidCartComponent } from './components/cart/invalid-cart/invalid-cart';
import { RegistrationFailedComponent } from './components/registration/registration-failed/registration-failed';
import { RegistrationCompletedComponent } from './components/registration/registration-completed/registration-completed';
import { AdminPanelComponent } from './components/admin-panel/admin-panel';
import { AddressConfirmationComponent } from './components/order/address-confirmation/address-confirmation';
import { OrderCancelledComponent } from './components/order/order-cancelled/order-cancelled';
import { PhoneConfirmationComponent } from './components/order/phone-confirmation/phone-confirmation';
import { OrderConfirmationComponent } from './components/order/order-confirmation/order-confirmation';
import { OrderCompletedComponent } from './components/order/order-completed/order-completed';
import { OrderListComponent } from './components/order/order-list/order-list';
import { OrderDetailsComponent } from './components/order/order-details/order-details';
import { ImageFormComponent } from './components/products/image-form/image-form';
import { CustomerListComponent } from './components/customers/customer-list/customer-list';
import { CustomerDetailsComponent } from './components/customers/customer-details/customer-details';

export const routes: Routes = [

    {
        path: `home`,
        component: HomeComponent,
        canActivate: [authGuard]
    },

    {
        path: `login`,
        component: LoginComponent,
        canActivate: [unidentifiedGuard]
    },

    {
        path: `admin`,
        component: AdminPanelComponent,
        canActivate: [adminGuard]
    },

    {
        path: `categories`,
        component: CategoryListComponent,
        canActivate: [adminGuard]
    },

    {
        path: `categories/addCategory`,
        component: CategoryFormComponent,
        canActivate: [adminGuard]
    },

    {
        path: `categories/updateCategory/:categoryId`,
        component: CategoryUpdateFormComponent,
        canActivate: [adminGuard]
    },

    {
        path: `products`,
        component: ProductListComponent,
        canActivate: [authGuard]
    },

    {
        path: `products/addProduct`,
        component: ProductFormComponent,
        canActivate: [adminGuard]
    },

    {
        path: `products/updateProduct/:productId`,
        component: ProductUpdateFormComponent,
        canActivate: [adminGuard]
    },

    {
        path: `products/productDetails/:productId`,
        component: ProductDetailsComponent,
        canActivate: [authGuard]
    },

    {
        path: `products/addImage/:productId`,
        component: ImageFormComponent,
        canActivate: [adminGuard]
    },

    {
        path: `customers`,
        component: CustomerListComponent,
        canActivate: [adminGuard]
    },

    {
        path: `customers/customerDetails/:customerId`,
        component: CustomerDetailsComponent,
        canActivate: [adminGuard]
    },

    {
        path: `register`,
        component: RegistrationFormComponent,
        canActivate: [unidentifiedGuard]
    },

    {
        path: `registration/completed`,
        component: RegistrationCompletedComponent,
        canActivate: [unidentifiedGuard]
    },

    {
        path: `registration/failed`,
        component: RegistrationFailedComponent,
        canActivate: [unidentifiedGuard]
    },

    {
        path: `cart`,
        component: CartComponent,
        canActivate: [userGuard]
    },

    {
        path: `cart/invalidCart`,
        component: InvalidCartComponent,
        canActivate: [userGuard]
    },

    {
        path: `order/addressConfirmation`,
        component: AddressConfirmationComponent,
        canActivate: [userGuard]
    },

    {
        path: `order/cancelled`,
        component: OrderCancelledComponent,
        canActivate: [userGuard]
    },

    {
        path: `order/phoneConfirmation`,
        component: PhoneConfirmationComponent,
        canActivate: [userGuard]
    },

    {
        path: `order/orderConfirmation`,
        component: OrderConfirmationComponent,
        canActivate: [userGuard]
    },

    {
        path: `order/completed`,
        component: OrderCompletedComponent,
        canActivate: [userGuard]
    },

    {
        path: `order/orderList`,
        component: OrderListComponent,
        canActivate: [adminGuard]
    },

    {
        path: `order/orderDetails/:orderId`,
        component: OrderDetailsComponent,
        canActivate: [adminGuard]
    },



    {
        path: `**`,
        redirectTo: `/home`,
        pathMatch: `full`
    }
];
