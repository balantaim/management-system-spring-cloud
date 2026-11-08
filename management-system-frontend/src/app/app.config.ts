import {ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter, withViewTransitions} from '@angular/router';

import {routes} from './app.routes';
import {provideOptimus} from '@openng/optimus-ui/config';
import Lara from '@openng/optimus-ui-themes/lara';
import {provideHttpClient, withInterceptors} from "@angular/common/http";
import {MessageService} from "@openng/optimus-ui/api";
import {errorInterceptor} from "./core/interceptors/error-interceptor";
import {authInterceptor} from "./core/interceptors/auth-interceptor";
import {AuthService} from "./core/services/auth.service";
import {platformInterceptor} from "./core/interceptors/platform-interceptor";


export const appConfig: ApplicationConfig = {
    providers: [
        provideBrowserGlobalErrorListeners(),
        provideRouter(routes, withViewTransitions()),
        // order matters: the error interceptor is outermost, so its retry passes through the auth interceptor again
        provideHttpClient(withInterceptors([
            errorInterceptor,
            platformInterceptor,
            authInterceptor
        ])),
        MessageService,
        provideAppInitializer(() => inject(AuthService).autoLogin()),
        // Optimus UI config
        provideOptimus({
            theme: {
                preset: Lara,
                options: {
                    darkModeSelector: '.app-dark',   // class toggled on <html> by ThemeService
                    cssLayer: {name: 'optimus', order: 'theme, base, optimus'},
                },
            }
        }),
    ],
};
