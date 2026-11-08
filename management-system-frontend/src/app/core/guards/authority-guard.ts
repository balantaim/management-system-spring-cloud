import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {NotificationService} from "../services/notification.service";
import {NO_PERMISSION_MESSAGE} from "../models/auth-result";

/** Use after authGuard: lets the user in only if the token has the given authority */
export const authorityGuard = (authority: string): CanActivateFn => () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    const notify = inject(NotificationService);

    if (auth.hasAuthority(authority)) return true;

    notify.warn(NO_PERMISSION_MESSAGE);
    return router.createUrlTree(['/home']);
};
