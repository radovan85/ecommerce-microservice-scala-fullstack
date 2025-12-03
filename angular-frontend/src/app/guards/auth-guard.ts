import { CanActivateFn, Router } from '@angular/router';

import { inject } from '@angular/core';
import { AuthService } from '../services/auth-service';

export var authGuard: CanActivateFn = (route, state) => {

  var authService = inject(AuthService);
  var router = inject(Router);
  var returnValue = false;

  if (authService.isAuthenticated()) {
    //console.log(`Auth guard: Identification confirmed!`);
    returnValue = true;
  } else {
    //console.log(`Auth guard: Identification failed!Navigating login`);
    router.navigate([`login`]);
  }

  return returnValue;
};