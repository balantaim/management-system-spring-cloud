import {computed, inject, Injectable, signal} from '@angular/core';
import {email, form, maxLength, minLength, pattern, required, submit, validate} from "@angular/forms/signals";
import {UiItem, UiSize} from "../models/ui-config";
import {TextField} from "../../shared/text-field/text-field";
import {PasswordStrength} from "../../shared/password-strength/password-strength";
import {SubmitButton} from "../../shared/submit-button/submit-button";
import {TIMEOUT_ERROR_MESSAGE, withTimeout} from "../utils/with-timeout";
import {AuthService} from "../services/auth.service";
import {Router} from "@angular/router";
import {RegisterRequest} from "../models/register-request";
import {
    EMAIL_MAX_LENGTH,
    FULL_NAME_MAX_LENGTH,
    FULL_NAME_MIN_LENGTH,
    FULL_NAME_PATTERN,
    HAS_LOWERCASE,
    HAS_NUMBER,
    HAS_SPECIAL,
    HAS_UPPERCASE,
    PASSWORD_MIN_LENGTH
} from "../validation/auth-rules";

const REGISTER_TIMEOUT_MS = 10_000;
// 'small' | 'large' | undefined (default)
const FIELD_SIZE: UiSize = undefined;
// 'small' | 'large' | undefined (default)
const BUTTON_SIZE: UiSize = undefined;

@Injectable()
export class RegisterController {
    private readonly auth = inject(AuthService);
    private readonly router = inject(Router);

    private readonly model = signal<RegisterRequest>({
        email: '',
        fullName: '',
        password: '',
        confirmPassword: '',
    });

    readonly form = form(this.model, (path) => {
        required(path.email, {message: 'Email is required'});
        email(path.email, {message: 'Enter a valid email address'});
        maxLength(path.email, EMAIL_MAX_LENGTH, {message: `Email must be at most ${EMAIL_MAX_LENGTH} characters`});

        required(path.fullName, {message: 'Full name is required'});
        minLength(path.fullName, FULL_NAME_MIN_LENGTH, {message: `At least ${FULL_NAME_MIN_LENGTH} characters`});
        maxLength(path.fullName, FULL_NAME_MAX_LENGTH, {message: `At most ${FULL_NAME_MAX_LENGTH} characters`});
        pattern(path.fullName, FULL_NAME_PATTERN, {
            message: 'Only letters, spaces, hyphens and apostrophes are allowed',
        });

        required(path.password, {message: 'Password is required'});
        minLength(path.password, PASSWORD_MIN_LENGTH, {message: `At least ${PASSWORD_MIN_LENGTH} characters`});
        pattern(path.password, HAS_UPPERCASE, {message: 'At least 1 uppercase letter'});
        pattern(path.password, HAS_LOWERCASE, {message: 'At least 1 lowercase letter'});
        pattern(path.password, HAS_NUMBER, {message: 'At least 1 number'});
        pattern(path.password, HAS_SPECIAL, {message: 'At least 1 special character'});

        required(path.confirmPassword, {message: 'Please repeat your password'});
        validate(path.confirmPassword, ({value, valueOf}) =>
            value() === valueOf(path.password)
                ? null
                : {kind: 'mismatch', message: 'Passwords do not match'},
        );
    });

    readonly submitError = signal('');

    readonly fields: UiItem[] = [
        {
            id: 'email',
            component: TextField,
            inputs: {
                field: this.form.email,
                id: 'email',
                label: 'Email',
                type: 'email',
                icon: 'pi pi-envelope',
                autocomplete: 'email',
                size: FIELD_SIZE,
            },
        },
        {
            id: 'fullName',
            component: TextField,
            inputs: {
                field: this.form.fullName,
                id: 'fullName',
                label: 'Full name',
                icon: 'pi pi-user',
                autocomplete: 'name',
                size: FIELD_SIZE,
            },
        },
        {
            id: 'password',
            component: TextField,
            inputs: {
                field: this.form.password,
                id: 'password',
                label: 'Password',
                type: 'password',
                icon: 'pi pi-lock',
                autocomplete: 'new-password',
                revealable: true,
                showMessages: false,   // the checklist below replaces the error text
                size: FIELD_SIZE,
            },
        },
        {
            id: 'passwordStrength',
            component: PasswordStrength,
            inputs: {field: this.form.password},
        },
        {
            id: 'confirmPassword',
            component: TextField,
            inputs: {
                field: this.form.confirmPassword,
                id: 'confirmPassword',
                label: 'Repeat password',
                type: 'password',
                icon: 'pi pi-lock',
                autocomplete: 'new-password',
                revealable: true,
                size: FIELD_SIZE,
            },
        },
    ];

    /** Disabled until the whole form is valid; the spinner shows while the request runs */
    readonly submitButton = computed<UiItem>(() => ({
        id: 'submit',
        component: SubmitButton,
        inputs: {
            label: 'Register',
            loading: this.form().submitting(),
            disabled: this.form().invalid(),
            size: BUTTON_SIZE,
        },
    }));

    async onSubmit(): Promise<void> {
        if (this.form().submitting()) return;
        this.submitError.set('');

        await submit(this.form, async () => {
            try {
                const success = await withTimeout(this.auth.register(this.model()), REGISTER_TIMEOUT_MS);
                if (!success) {
                    this.submitError.set('This email is already registered.');
                    return;
                }
                await this.router.navigateByUrl('/sign-in');
            } catch (error) {
                const timedOut = error instanceof Error && error.message === TIMEOUT_ERROR_MESSAGE;
                this.submitError.set(timedOut
                    ? 'The request timed out. Please try again.'
                    : 'Something went wrong. Please try again.');
            }
        });
    }
}
