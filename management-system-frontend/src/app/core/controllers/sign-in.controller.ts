import {computed, inject, Injectable, signal} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {LoginRequest} from "../models/login-request";
import {UiItem, UiSize} from "../models/ui-config";
import {TextField} from "../../shared/text-field/text-field";
import {SubmitButton} from "../../shared/submit-button/submit-button";
import {TIMEOUT_ERROR_MESSAGE, withTimeout} from '../utils/with-timeout';
import {
    EMAIL_MAX_LENGTH,
    HAS_LOWERCASE,
    HAS_NUMBER,
    HAS_SPECIAL,
    HAS_UPPERCASE,
    PASSWORD_MAX_LENGTH,
    PASSWORD_MIN_LENGTH
} from "../validation/auth-rules";
import {
    form,
    email,
    maxLength,
    minLength,
    pattern,
    required,
    submit
} from "@angular/forms/signals";
import {NotificationService} from "../services/notification.service";


const LOGIN_TIMEOUT_MS = 10_000;
// 'small' | 'large' | undefined (default)
const FIELD_SIZE: UiSize = undefined;
// 'small' | 'large' | undefined (default)
const BUTTON_SIZE: UiSize = undefined;

@Injectable()
export class SignInController {

    private readonly auth = inject(AuthService);
    private readonly notify = inject(NotificationService);
    private readonly router = inject(Router);
    private readonly route = inject(ActivatedRoute);

    private readonly model = signal<LoginRequest>({email: '', password: ''});

    readonly form = form(this.model, (path) => {
        required(path.email, {message: 'Email is required'});
        email(path.email, {message: 'Enter a valid email address'});
        maxLength(path.email, EMAIL_MAX_LENGTH, {message: `Email must be at most ${EMAIL_MAX_LENGTH} characters`});

        required(path.password, {message: 'Password is required'});
        minLength(path.password, PASSWORD_MIN_LENGTH, {message: `At least ${PASSWORD_MIN_LENGTH} characters`});
        maxLength(path.password, PASSWORD_MAX_LENGTH, {message: `Password must be at most ${PASSWORD_MAX_LENGTH} characters`});
        pattern(path.password, HAS_UPPERCASE, {message: 'At least 1 uppercase letter'});
        pattern(path.password, HAS_LOWERCASE, {message: 'At least 1 lowercase letter'});
        pattern(path.password, HAS_NUMBER, {message: 'At least 1 number'});
        pattern(path.password, HAS_SPECIAL, {message: 'At least 1 special character'});
    });

    /** Components loaded dynamically by the view, with their parameters */
    readonly fields: UiItem[] = [
        {
            id: 'email',
            component: TextField,
            inputs: {
                field: this.form.email,
                id: 'email',
                label: 'Email',
                type: 'email',
                icon: 'pi pi-user',
                autocomplete: 'username',
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
                autocomplete: 'current-password',
                revealable: true,
                size: FIELD_SIZE,
            },
        },
    ];

    /** A computed, so the button gets a new "loading" value whenever the submit state changes */
    readonly submitButton = computed<UiItem>(() => ({
        id: 'submit',
        component: SubmitButton,
        inputs: {
            label: 'Sign in',
            loading: this.form().submitting(),
            disabled: this.form().invalid(),
            size: BUTTON_SIZE,
        },
    }));

    async onSubmit(): Promise<void> {
        if (this.form().submitting()) return;

        await submit(this.form, async () => {
            const result = await this.auth.login(this.model());

            if (result.status === 'success') {
                this.notify.success(result.message);
                await this.router.navigateByUrl(this.returnUrl());
            } else if (result.status === 'too_many_requests') {
                this.notify.warn(result.message);
            } else {
                this.notify.error(result.message);
            }
        });
    }

    /** Only internal paths are allowed, so a crafted link can't redirect to another site */
    private returnUrl(): string {
        const url = this.route.snapshot.queryParamMap.get('returnUrl');
        return url && url.startsWith('/') && !url.startsWith('//') ? url : '/gallery';
    }

}
