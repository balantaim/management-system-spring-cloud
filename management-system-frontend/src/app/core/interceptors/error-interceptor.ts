import {HttpErrorResponse, HttpEvent, HttpHandlerFn, HttpInterceptorFn, HttpRequest} from '@angular/common/http';
import {inject} from "@angular/core";
import {from, lastValueFrom} from "rxjs";
import {IS_RETRY, PUBLIC_REQUEST} from "../http/http-tokens";
import {classify} from "../http/http-error";
import {HttpErrorHandlerService} from "../http/http-error-handler.service";
import {UnauthorizedHandlerService} from "../http/unauthorized-handler.service";


interface Handlers {
    errors: HttpErrorHandlerService;
    unauthorized: UnauthorizedHandlerService;
}

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
    // inject() only works synchronously, so the handlers are collected before the async part starts
    const handlers: Handlers = {
        errors: inject(HttpErrorHandlerService),
        unauthorized: inject(UnauthorizedHandlerService),
    };
    return from(send(req, next, handlers));
};

async function send(req: HttpRequest<unknown>, next: HttpHandlerFn, handlers: Handlers): Promise<HttpEvent<unknown>> {
    try {
        return await lastValueFrom(next(req));
    } catch (error) {
        if (!(error instanceof HttpErrorResponse)) throw error;

        // login and refresh: the caller decides what the user sees
        if (req.context.get(PUBLIC_REQUEST)) {
            handlers.errors.log(error, req);
            throw error;
        }

        if (classify(error) === 'unauthorized') {
            handlers.errors.log(error, req);
            if (await handlers.unauthorized.recover(req)) {
                // the repeated request passes through the other interceptors again and gets the new token
                return send(req.clone({context: req.context.set(IS_RETRY, true)}), next, handlers);
            }
            throw error;
        }

        handlers.errors.handle(error, req);
        throw error;
    }

}