import {inject, Service} from '@angular/core';
import {AUTH_MESSAGES, NO_PERMISSION_MESSAGE} from "../models/auth-result";
import {HttpErrorResponse, HttpRequest} from "@angular/common/http";
import {classify} from "./http-error";
import {RequestTimeoutError} from "./api-client.service";
import {LoggerService} from "../services/logger.service";
import {NotificationService} from "../services/notification.service";

@Service()
export class HttpErrorHandlerService {

    private readonly logger = inject(LoggerService);
    private readonly notifications = inject(NotificationService);

    /** Logs the error and shows the matching toast. Safe to call from any service or component. */
    handle(error: unknown, req?: HttpRequest<unknown>): void {
        this.log(error, req);
        this.notify(error);
    }

    /** Console only: the real details (status, server message) never reach the user */
    log(error: unknown, req?: HttpRequest<unknown>): void {
        const target = req ? `${req.method} ${req.url}` : 'request';

        if (error instanceof HttpErrorResponse) {
            const details = {status: error.status, statusText: error.statusText, body: error.error};
            const message = `HTTP ${target} failed with status ${error.status}`;
            if (classify(error) === 'server') {
                this.logger.error(message, details);
            } else {
                this.logger.warn(message, details);
            }
            return;
        }

        // timeouts are already logged by ApiClient
        if (error instanceof RequestTimeoutError) return;

        this.logger.error(`Unexpected error in ${target}`, error);
    }

    /**
     * Toast only. Errors that the calling code should explain itself (400, other 4xx)
     * and 401 (handled by UnauthorizedHandler) show nothing here.
     */
    notify(error: unknown): void {
        if (error instanceof RequestTimeoutError) {
            this.notifications.warn(AUTH_MESSAGES.timeout);
            return;
        }
        if (!(error instanceof HttpErrorResponse)) return;

        switch (classify(error)) {
            case 'server':
                this.notifications.error(AUTH_MESSAGES.server_error);
                break;
            case 'forbidden':
                this.notifications.error(NO_PERMISSION_MESSAGE);
                break;
            case 'account_locked':
                this.notifications.warn(AUTH_MESSAGES.account_locked);
                break;
            case 'too_many_requests':
                this.notifications.warn(AUTH_MESSAGES.too_many_requests);
                break;
        }
    }

}
