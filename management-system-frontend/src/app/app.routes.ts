import {Routes} from '@angular/router';
import {guestGuard} from "./core/guards/guest-guard";
import {authGuard} from "./core/guards/auth-guard";
import {authorityGuard} from "./core/guards/authority-guard";
import {AUTHORITY} from "./core/models/authority";

export const routes: Routes = [
    {
        path: '',
        pathMatch: 'full',
        redirectTo: 'home'
    },
    {
        path: 'home',
        title: 'Home',
        loadComponent: () => import('./pages/home/home').then(m => m.Home)
    },
    {
        path: 'demos',
        title: 'Demos',
        loadComponent: () => import('./pages/demos/demos').then(m => m.Demos)
    },
    {
        path: 'sign-in',
        title: 'Sign in',
        canActivate: [guestGuard],
        loadComponent: () => import('./pages/sign-in/sign-in').then(m => m.SignIn)
    },
    {
        path: 'register',
        title: 'Register',
        canActivate: [guestGuard],
        loadComponent: () => import('./pages/register/register').then(m => m.Register)
    },
    //Only for logged users
    {
        path: 'gallery',
        title: 'Gallery',
        canActivate: [authGuard, authorityGuard(AUTHORITY.customer)],
        loadComponent: () => import('./pages/gallery-page/gallery-page').then(m => m.GalleryPage)
    },
    {
        path: 'account',
        title: 'Account',
        canActivate: [authGuard],
        loadComponent: () => import('./pages/account/account').then(m => m.Account)
    },
    {
        path: 'admin',
        title: 'Administration',
        canActivate: [authGuard, authorityGuard(AUTHORITY.admin)],
        loadComponent: () => import('./pages/admin/admin').then(m => m.Admin),
    },
    //Default page (Error 404)
    {
        path: '**',
        title: 'Page not found',
        loadComponent: () => import('./pages/not-found/not-found').then(m => m.NotFound)
    },
];
