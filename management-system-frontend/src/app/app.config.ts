import {ApplicationConfig, provideBrowserGlobalErrorListeners} from '@angular/core';
import {provideRouter, withViewTransitions} from '@angular/router';

import {routes} from './app.routes';
import {provideOptimus} from '@openng/optimus-ui/config';
import Lara from '@openng/optimus-ui-themes/lara';


export const appConfig: ApplicationConfig = {
    providers: [
        provideBrowserGlobalErrorListeners(),
        provideRouter(routes, withViewTransitions()),
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
