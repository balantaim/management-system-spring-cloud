import {HttpInterceptorFn} from '@angular/common/http';
import {environment} from "../../../environments/environment";


const CLIENT_PLATFORM_HEADER = 'Client-Platform-Id';
const WRITE_METHODS = new Set(['POST', 'PUT', 'PATCH', 'DELETE']);

/** Every write request to our backend carries the platform id, including login and refresh */
export const platformInterceptor: HttpInterceptorFn = (req, next) =>
    WRITE_METHODS.has(req.method)
        ? next(req.clone({setHeaders: {[CLIENT_PLATFORM_HEADER]: environment.clientPlatformId}}))
        : next(req);