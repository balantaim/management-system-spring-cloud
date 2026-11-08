import {inject, Service} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {Router} from "@angular/router";
import {NotificationService} from "../services/notification.service";
import {HttpRequest} from "@angular/common/http";
import {IS_RETRY} from "./http-tokens";
import {AUTH_MESSAGES, SIGN_IN_AGAIN_MESSAGE} from "../models/auth-result";


@Service()
export class UnauthorizedHandlerService {

    private readonly auth = inject(AuthService);
    private readonly router = inject(Router);
    private readonly notifications = inject(NotificationService);

    /** Several requests can fail together; the user is told (and redirected) only once */
    private loggedOutNotified = false;

    /** Resolves to true when a fresh token is available and the request can be repeated */
    async recover(req: HttpRequest<unknown>): Promise<boolean> {
        if (this.auth.isLoggedIn()) this.loggedOutNotified = false;

        const alreadyRetried = req.context.get(IS_RETRY);
        if (!alreadyRetried && (await this.auth.refresh())) return true;

        // a fresh token was rejected too, so it is not usable
        if (alreadyRetried) this.auth.logout();

        if (this.auth.isLoggedIn()) {
            // the refresh failed only because the server could not be reached; the user stays logged in
            this.notifications.error(AUTH_MESSAGES.server_error);
        } else {
            this.redirectToSignIn();
        }
        return false;
    }

    private redirectToSignIn(): void {
        if (this.loggedOutNotified) return;
        this.loggedOutNotified = true;
        this.notifications.warn(SIGN_IN_AGAIN_MESSAGE);
        void this.router.navigate(['/sign-in'], {queryParams: {returnUrl: this.router.url}});
    }

}
