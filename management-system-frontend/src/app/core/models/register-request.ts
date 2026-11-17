export interface RegisterRequest {
    email: string;
    fullName: string;
    password: string;
    confirmPassword: string;
}

/** What the backend receives: exactly { email, fullName, password }. confirmPassword never leaves the form. */
export type RegisterPayload = Omit<RegisterRequest, 'confirmPassword'>;
