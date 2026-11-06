import {Routes} from '@angular/router';
import {guestGuard} from "./core/guards/guest-guard";
import {authGuard} from "./core/guards/auth-guard";

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home'
    },
    {
        path: 'home',
        loadComponent: () => import('./pages/home/home').then(m => m.Home)
    },
    {
        path: 'demo',
        loadComponent: () => import('./pages/demo/demo').then(m => m.Demo)
    },
    {
        path: 'sign-in',
        canActivate: [guestGuard],
        loadComponent: () => import('./pages/sign-in/sign-in').then(m => m.SignIn)
    },
    {
        path: 'register',
        canActivate: [guestGuard],
        loadComponent: () => import('./pages/register/register').then(m => m.Register)
    },
    {
        path: 'gallery',
        canActivate: [authGuard],
        loadComponent: () => import('./pages/gallery-page/gallery-page').then(m => m.GalleryPage),
    },
    {
        path: 'account',
        canActivate: [authGuard],
        loadComponent: () => import('./pages/account/account').then(m => m.Account),
    },
    {
        path: '**',
        redirectTo: 'home'
    },
];
