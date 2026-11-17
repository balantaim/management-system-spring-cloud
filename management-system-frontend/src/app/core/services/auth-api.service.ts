import {inject, Service} from '@angular/core';
import {RefreshResponse, TokenResponse} from "../models/token-response";
import {HttpErrorResponse, HttpResponse} from "@angular/common/http";
import {AuthError} from "../models/auth-result";
import {ApiClientService, RequestTimeoutError} from "../http/api-client.service";
import {LoggerService} from "./logger.service";
import {classify} from "../http/http-error";
import {LoginRequest} from "../models/login-request";
import {RefreshRequest} from "../models/refresh-request";


const LOGIN_PATH = '/auth/login';
const REFRESH_PATH = '/auth/refresh';
const USER_ID_HEADER = 'User-Id';

export interface AuthResponse<T = TokenResponse> {
    tokens: T;
    userId: string | null;
}

@Service()
export class AuthApiService {

    private readonly api = inject(ApiClientService);
    private readonly logger = inject(LoggerService);

    async login(request: LoginRequest): Promise<AuthResponse> {
        // Credentials go in the Authorization header (Basic email:password), no body
        const response = await this.call<TokenResponse>(LOGIN_PATH, null, {
            Authorization: `Basic ${this.basic(request.email, request.password)}`,
        });

        const tokens = response.body;
        if (!tokens?.access_token || !tokens.refresh_token) {
            this.logger.error('Login response has no access_token/refresh_token');
            throw new AuthError('server_error');
        }
        return {tokens, userId: response.headers.get(USER_ID_HEADER)};
    }

    async refresh(refreshToken: string): Promise<AuthResponse<RefreshResponse>> {
        const body: RefreshRequest = {refresh_token: refreshToken};
        const response = await this.call<RefreshResponse>(REFRESH_PATH, body);

        const tokens = response.body;
        if (!tokens?.access_token) {
            this.logger.error('Refresh response has no access_token');
            throw new AuthError('server_error');
        }
        return {tokens, userId: response.headers.get(USER_ID_HEADER)};
    }

    private async call<T>(
        path: string,
        body: RefreshRequest | null = null,
        headers?: Record<string, string>,
    ): Promise<HttpResponse<T>> {
        try {
            return await this.api.post<T>(path, body, {public: true, headers});
        } catch (error) {
            throw this.toAuthError(error);
        }
    }

    /** UTF-8 safe base64 (plain btoa breaks on non-ASCII characters) */
    private basic(email: string, password: string): string {
        const bytes = new TextEncoder().encode(`${email}:${password}`);
        return btoa(String.fromCharCode(...bytes));
    }

    /** The error interceptor has already logged HTTP errors for these public calls */
    private toAuthError(error: unknown): AuthError {
        if (error instanceof RequestTimeoutError) {
            return new AuthError('timeout');
        }

        if (error instanceof HttpErrorResponse) {
            switch (classify(error)) {
                case 'bad_request':
                case 'unauthorized':
                case 'forbidden':
                    return new AuthError('wrong_credentials');
                case 'account_locked':
                    return new AuthError('account_locked');
                case 'too_many_requests':
                    return new AuthError('too_many_requests');
                default:
                    return new AuthError('server_error');
            }
        }

        this.logger.error('Unexpected error during an auth request', error);
        return new AuthError('server_error');
    }

}