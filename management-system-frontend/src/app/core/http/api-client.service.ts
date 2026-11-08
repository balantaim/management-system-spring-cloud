import {inject, Service} from '@angular/core';
import {Observable} from "rxjs";
import {HttpClient, HttpContext, HttpResponse} from "@angular/common/http";
import {LoggerService} from "../services/logger.service";
import {PUBLIC_REQUEST} from "./http-tokens";
import {environment} from "../../../environments/environment";


const DEFAULT_TIMEOUT_MS = 10_000;

export class RequestTimeoutError extends Error {
    constructor() {
        super('Request timed out');
        this.name = 'RequestTimeoutError';
    }
}

export interface ApiRequestOptions {
    /** No Bearer header, no token refresh, no global error toast (login, refresh) */
    public?: boolean;
    timeoutMs?: number;
}

@Service()
export class ApiClientService {

    private readonly http = inject(HttpClient);
    private readonly logger = inject(LoggerService);

    get<T>(path: string, options?: ApiRequestOptions): Promise<HttpResponse<T>> {
        return this.request<T>('GET', path, undefined, options);
    }

    post<T>(path: string, body: unknown, options?: ApiRequestOptions): Promise<HttpResponse<T>> {
        return this.request<T>('POST', path, body, options);
    }

    put<T>(path: string, body: unknown, options?: ApiRequestOptions): Promise<HttpResponse<T>> {
        return this.request<T>('PUT', path, body, options);
    }

    delete<T>(path: string, options?: ApiRequestOptions): Promise<HttpResponse<T>> {
        return this.request<T>('DELETE', path, undefined, options);
    }

    private request<T>(
        method: string,
        path: string,
        body: unknown,
        options: ApiRequestOptions = {},
    ): Promise<HttpResponse<T>> {
        const context = new HttpContext().set(PUBLIC_REQUEST, options.public ?? false);
        const source = this.http.request<T>(method, `${environment.apiUrl}${path}`, {
            body,
            observe: 'response',
            context,
        });
        return this.toPromise(source, `${method} ${path}`, options.timeoutMs ?? DEFAULT_TIMEOUT_MS);
    }

    private toPromise<T>(source: Observable<HttpResponse<T>>, label: string, timeoutMs: number): Promise<HttpResponse<T>> {
        return new Promise<HttpResponse<T>>((resolve, reject) => {
            let timer: ReturnType<typeof setTimeout>;

            const subscription = source.subscribe({
                next: (response) => {
                    clearTimeout(timer);
                    resolve(response);
                },
                error: (error: unknown) => {
                    clearTimeout(timer);
                    reject(error);
                },
            });

            timer = setTimeout(() => {
                subscription.unsubscribe(); // cancels the HTTP request
                this.logger.warn(`${label} timed out after ${timeoutMs} ms`);
                reject(new RequestTimeoutError());
            }, timeoutMs);
        });
    }

}
