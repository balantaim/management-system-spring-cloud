import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";

export const authGuard: CanActivateFn = (_route, state) => {
    const auth = inject(AuthService);
    const router = inject(Router);

    return auth.isLoggedIn()
        ? true
        : router.createUrlTree(['/sign-in'], {queryParams: {returnUrl: state.url}});
};