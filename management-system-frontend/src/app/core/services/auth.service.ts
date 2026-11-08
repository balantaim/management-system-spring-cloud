import {computed, inject, Service, signal} from '@angular/core';
import {LoginRequest} from "../models/login-request";
import {RegisterRequest} from "../models/register-request";
import {AuthUser} from "../models/auth-user";
import {AUTH_MESSAGES, AuthError, AuthResult, AuthStatus} from "../models/auth-result";
import {isTokenExpired, readAuthorities} from "../utils/jwt";
import {AUTHORITY} from "../models/authority";
import {AuthApiService} from "./auth-api.service";
import {TokenStorageService} from "./token-storage.service";
import {LoggerService} from "./logger.service";

@Service()
export class AuthService {

    private readonly api = inject(AuthApiService);
    private readonly storage = inject(TokenStorageService);
    private readonly logger = inject(LoggerService);

    private readonly currentUser = signal<AuthUser | null>(null);
    private readonly accessToken = signal<string | null>(null);

    readonly user = this.currentUser.asReadonly();
    readonly isLoggedIn = computed(() => this.currentUser() !== null);

    /** Read from the access token, so they follow every refresh. UI only: the server enforces access. */
    readonly authorities = computed(() => {
        const token = this.accessToken();
        return token ? readAuthorities(token) : [];
    });
    readonly isAdmin = computed(() => this.authorities().includes(AUTHORITY.admin));
    readonly isCustomer = computed(() => this.authorities().includes(AUTHORITY.customer));

    /** Shared by all callers, so several expired requests trigger only one refresh */
    private refreshing: Promise<boolean> | null = null;

    hasAuthority(authority: string): boolean {
        return this.authorities().includes(authority);
    }

    /** Never throws: every outcome is an AuthResult with the text to show */
    async login(request: LoginRequest): Promise<AuthResult> {
        const email = request.email.trim();
        try {
            const response = await this.api.login({email, password: request.password});
            this.storage.saveTokens(response.tokens, response.userId);
            this.setUser(response.tokens.access_token, {email, fullName: this.nameFromEmail(email)});
            return {status: 'success', message: AUTH_MESSAGES.success};
        } catch (error) {
            if (!(error instanceof AuthError)) this.logger.error('Unexpected login error', error);
            const status: AuthStatus = error instanceof AuthError ? error.status : 'server_error';
            return {status, message: AUTH_MESSAGES[status]};
        }
    }

    /** Autologin: runs once at startup, before the first route is activated */
    async autoLogin(): Promise<void> {
        const accessToken = this.storage.accessToken;

        if (accessToken && !isTokenExpired(accessToken)) {
            this.accessToken.set(accessToken);
            this.currentUser.set(this.storedUser());
            return;
        }

        if (this.storage.refreshToken) {
            await this.refresh();
            return;
        }

        this.storage.clear();
    }

    /** Gets a new access token with the refresh token. Resolves to true when it worked. */
    refresh(): Promise<boolean> {
        this.refreshing ??= this.requestNewTokens().finally(() => {
            this.refreshing = null;
        });
        return this.refreshing;
    }

    logout(): void {
        this.storage.clear();
        this.accessToken.set(null);
        this.currentUser.set(null);
    }

    /** Still a demo. Later: move to AuthApiService and call the backend. */
    async register(request: RegisterRequest): Promise<boolean> {
        await new Promise((resolve) => setTimeout(resolve, 600));
        return request.email !== 'taken@example.com';
    }

    private async requestNewTokens(): Promise<boolean> {
        const refreshToken = this.storage.refreshToken;
        if (!refreshToken) return false;

        try {
            const response = await this.api.refresh(refreshToken);
            this.storage.saveTokens(response.tokens, response.userId);
            this.setUser(response.tokens.access_token, this.storedUser());
            return true;
        } catch (error) {
            // the server rejected the refresh token, so log out.
            // A network or server failure keeps the tokens, so a later attempt can still work.
            if (error instanceof AuthError && (error.status === 'wrong_credentials' || error.status === 'account_locked')) {
                this.logout();
            }
            return false;
        }
    }

    private setUser(accessToken: string, user: AuthUser): void {
        this.accessToken.set(accessToken);
        this.storage.saveUser(user);
        this.currentUser.set(user);
    }

    private storedUser(): AuthUser {
        return this.storage.user ?? {email: '', fullName: 'User'};
    }

    /** "john.doe@example.com" becomes "John Doe" */
    private nameFromEmail(email: string): string {
        const name = email
            .split('@')[0]
            .split(/[._-]+/)
            .filter(Boolean)
            .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
            .join(' ');
        return name || 'User';
    }

}
