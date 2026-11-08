import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from "@angular/core";
import {PUBLIC_REQUEST} from "../http/http-tokens";
import {TokenStorageService} from "../services/token-storage.service";


/** The Bearer token goes only to guarded requests to our own backend */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    if (req.context.get(PUBLIC_REQUEST)) {
        return next(req);
    }

    const token = inject(TokenStorageService).accessToken;
    return next(token ? req.clone({setHeaders: {Authorization: `Bearer ${token}`}}) : req);
};
