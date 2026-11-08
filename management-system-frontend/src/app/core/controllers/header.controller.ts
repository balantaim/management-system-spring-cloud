import {Injectable, inject, signal, computed} from '@angular/core';
import {NavItem} from '../models/nav-item';
import {ThemeMode} from '../models/theme-mode';
import {ThemeService} from '../services/theme.service';
import {AuthService} from "../services/auth.service";
import {Router} from "@angular/router";
import {DropdownOption} from "../models/dropdown-option";


// provided per component, not root
@Injectable()
export class HeaderController {

    private readonly theme = inject(ThemeService);
    private readonly auth = inject(AuthService);
    private readonly router = inject(Router);

    readonly isLoggedIn = this.auth.isLoggedIn;
    readonly user = this.auth.user;

    readonly navItems = computed<NavItem[]>(() => {
        const items: NavItem[] = [{label: 'Home', route: '/home', icon: 'pi pi-home'}];

        if (this.auth.isCustomer()) {
            items.push({label: 'Gallery', route: '/gallery', icon: 'pi pi-image'});
        }

        items.push({label: 'Demo', route: '/demos', icon: 'pi pi-th-large'}); // keep the icon you chose

        if (!this.auth.isLoggedIn()) {
            items.push({label: 'Sign in', route: '/sign-in', icon: 'pi pi-sign-in'});
        }

        if (this.auth.isAdmin()) {
            items.push({label: 'Administration', route: '/admin', icon: 'pi pi-shield'});
        }
        return items;
    });

    readonly themeOptions: DropdownOption<ThemeMode>[] = [
        {label: 'System', value: ThemeMode.SYSTEM, icon: 'pi pi-palette'},
        {label: 'White', value: ThemeMode.LIGHT, icon: 'pi pi-sun'},
        {label: 'Dark', value: ThemeMode.DARK, icon: 'pi pi-moon'},
    ];

    readonly menuOpen = signal(false);
    readonly userMenuOpen = signal(false);
    readonly themeMode = this.theme.mode;

    readonly initial = computed(() => this.auth.user()?.fullName.trim().charAt(0).toUpperCase() ?? '');

    toggleMenu(): void {
        this.menuOpen.update((open) => !open);
    }

    closeMenu(): void {
        this.menuOpen.set(false);
    }

    toggleUserMenu(): void {
        this.userMenuOpen.update((open) => !open);
    }

    closeUserMenu(): void {
        this.userMenuOpen.set(false);
    }

    setTheme(mode: ThemeMode): void {
        this.theme.setMode(mode);
    }

    async logout(): Promise<void> {
        this.closeUserMenu();
        this.closeMenu();
        this.auth.logout();
        await this.router.navigateByUrl('/home');
    }

}
