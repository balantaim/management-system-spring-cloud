import {Injectable, inject, signal, computed} from '@angular/core';
import {NavItem} from '../models/nav-item';
import {ThemeMode} from '../models/theme-mode';
import {ThemeService} from '../services/theme.service';
import {AuthService} from "../services/auth.service";
import {Router} from "@angular/router";


// provided per component, not root
@Injectable()
export class HeaderController {

    private readonly theme = inject(ThemeService);
    private readonly auth = inject(AuthService);
    private readonly router = inject(Router);

    readonly isLoggedIn = this.auth.isLoggedIn;
    readonly user = this.auth.user;

    readonly navItems = computed<NavItem[]>(() => [
        { label: 'Home', route: '/home', icon: 'pi pi-home' },
        this.auth.isLoggedIn()
            ? { label: 'Gallery', route: '/gallery', icon: 'pi pi-image' }
            : { label: 'Demo', route: '/demo', icon: 'pi pi-desktop' },
        { label: 'Sign in', route: '/sign-in', icon: 'pi pi-sign-in' },
    ]);

    readonly themeOptions = [
        {label: 'System', value: ThemeMode.SYSTEM},
        {label: 'White', value: ThemeMode.LIGHT},
        {label: 'Dark', value: ThemeMode.DARK},
    ];

    readonly menuOpen = signal(false);
    readonly themeMode = this.theme.mode;
    readonly userMenuOpen = signal(false);

    readonly initial = computed(() =>
        this.auth.user()?.fullName.trim().charAt(0).toUpperCase() ?? '');

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
