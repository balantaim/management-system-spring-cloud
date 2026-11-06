import {computed, Service, signal} from '@angular/core';
import {LoginRequest} from "../models/login-request";
import {RegisterRequest} from "../models/register-request";
import {AuthUser} from "../models/auth-user";

@Service()
export class AuthService {

    private readonly currentUser = signal<AuthUser | null>(null);

    /** In-memory only. Later: replace with JWT access/refresh tokens. */
    readonly user = this.currentUser.asReadonly();
    readonly isLoggedIn = computed(() => this.currentUser() !== null);

    /** Demo only: remembers names of users registered during this session */
    private readonly registeredNames = new Map<string, string>();

    async login(request: LoginRequest): Promise<boolean> {
        await this.delay(2000);
        // demo only: this address simulates a failed login
        if (request.email === 'wrong@example.com') return false;

        const email = request.email.trim();
        const fullName = this.registeredNames.get(email.toLowerCase()) ?? this.nameFromEmail(email);
        this.currentUser.set({email, fullName});
        return true;
    }

    async register(request: RegisterRequest): Promise<boolean> {
        await this.delay(2000);
        // demo only: this address simulates "already registered"
        if (request.email === 'taken@example.com') return false;

        this.registeredNames.set(request.email.trim().toLowerCase(), request.fullName.trim());
        return true;
    }

    logout(): void {
        this.currentUser.set(null);
    }

    private delay(ms: number): Promise<void> {
        return new Promise((resolve) => setTimeout(resolve, ms));
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
