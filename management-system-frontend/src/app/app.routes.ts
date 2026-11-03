import {Routes} from '@angular/router';

export const routes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: 'home'},
    {
        path: 'home', loadComponent: () =>
            import('./pages/home/home/home').then(m => m.Home)
    },
    {
        path: 'sign-in', loadComponent: () =>
            import('./pages/sign-in/sign-in/sign-in').then(m => m.SignIn)
    },
    {
        path: 'demo', loadComponent: () =>
            import('./pages/demo/demo/demo').then(m => m.Demo)
    },
    {
        path: 'register', loadComponent: () =>
            import('./pages/register/register/register').then(m => m.Register)
    },
    {path: '**', redirectTo: 'home'},
];
