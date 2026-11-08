import {HttpErrorResponse} from "@angular/common/http";

export type HttpErrorKind =
    | 'bad_request'
    | 'unauthorized'
    | 'forbidden'
    | 'account_locked'
    | 'too_many_requests'
    | 'server'
    | 'client';

export class HttpError {
}

/** The only place that knows what a status code means. Status 0 is a network failure. */
export function classify(error: HttpErrorResponse): HttpErrorKind {
    const {status} = error;
    if (status === 0 || status >= 500) return 'server';

    switch (status) {
        case 400:
            return 'bad_request';
        case 401:
            return 'unauthorized';
        case 403:
            return 'forbidden';
        case 423:
            return 'account_locked';
        case 429:
            return 'too_many_requests';
        default:
            return 'client';
    }

}