// Auth
export type AuthStatus =
    | 'success'
    | 'wrong_credentials'
    | 'too_many_requests'
    | 'account_locked'
    | 'timeout'
    | 'server_error';

/** The texts users see. The real server error is only written to the console. */
export const AUTH_MESSAGES: Record<AuthStatus, string> = {
    success: 'Success',
    wrong_credentials: 'Wrong username or password',
    too_many_requests: 'Too many requests, please try again later',
    account_locked: 'Account locked',
    timeout: 'Timeout',
    server_error: 'Server is unavailable right now, please try later',
};

export const NO_PERMISSION_MESSAGE = 'You do not have permission to do this';
export const SIGN_IN_AGAIN_MESSAGE = 'Your login has expired, please sign in again';

export interface AuthResult {
    status: AuthStatus;
    message: string;
}

/** Thrown by AuthApiService; AuthService turns it into an AuthResult */
export class AuthError extends Error {
    constructor(readonly status: Exclude<AuthStatus, 'success'>) {
        super(status);
        this.name = 'AuthError';
    }
}

// Register
export type RegisterStatus =
    | 'success'
    | 'email_taken'
    | 'invalid_data'
    | 'too_many_requests'
    | 'timeout'
    | 'server_error';

export const REGISTER_MESSAGES: Record<RegisterStatus, string> = {
    success: 'Account created, you can sign in now',
    email_taken: 'This email is already registered',
    invalid_data: 'Please check the entered data and try again',
    too_many_requests: AUTH_MESSAGES.too_many_requests,
    timeout: AUTH_MESSAGES.timeout,
    server_error: AUTH_MESSAGES.server_error,
};

export interface RegisterResult {
    status: RegisterStatus;
    message: string;
}

/** Thrown by AuthApiService; AuthService turns it into a RegisterResult */
export class RegisterError extends Error {
    constructor(readonly status: Exclude<RegisterStatus, 'success'>) {
        super(status);
        this.name = 'RegisterError';
    }
}