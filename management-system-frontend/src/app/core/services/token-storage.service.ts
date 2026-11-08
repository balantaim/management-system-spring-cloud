import {Service} from '@angular/core';
import {AuthUser} from "../models/auth-user";
import {RefreshResponse} from "../models/token-response";


const KEYS = {
    accessToken: 'ms.access_token',
    refreshToken: 'ms.refresh_token',
    userId: 'ms.user_id',
    user: 'ms.user',
} as const;

@Service()
export class TokenStorageService {

    get accessToken(): string | null {
        return this.read(KEYS.accessToken);
    }

    get refreshToken(): string | null {
        return this.read(KEYS.refreshToken);
    }

    get userId(): string | null {
        return this.read(KEYS.userId);
    }

    get user(): AuthUser | null {
        const raw = this.read(KEYS.user);
        if (!raw) return null;
        try {
            const value = JSON.parse(raw) as Partial<AuthUser>;
            return typeof value.email === 'string' && typeof value.fullName === 'string'
                ? {email: value.email, fullName: value.fullName}
                : null;
        } catch {
            return null;
        }
    }

    saveTokens(tokens: RefreshResponse, userId: string | null): void {
        this.write(KEYS.accessToken, tokens.access_token);
        if (tokens.refresh_token) this.write(KEYS.refreshToken, tokens.refresh_token);
        if (userId) this.write(KEYS.userId, userId);
    }

    saveUser(user: AuthUser): void {
        this.write(KEYS.user, JSON.stringify(user));
    }

    clear(): void {
        Object.values(KEYS).forEach((key) => {
            try {
                localStorage.removeItem(key);
            } catch {
                // storage can be blocked (private mode); nothing to clear then
            }
        });
    }

    private read(key: string): string | null {
        try {
            return localStorage.getItem(key);
        } catch {
            return null;
        }
    }

    private write(key: string, value: string): void {
        try {
            localStorage.setItem(key, value);
        } catch {
            // storage can be full or blocked; the session then lasts until the page is closed
        }
    }

}
