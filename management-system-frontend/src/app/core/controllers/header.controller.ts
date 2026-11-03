import {Injectable, inject, signal} from '@angular/core';
import {NavItem} from '../models/nav-item';
import {ThemeMode} from '../models/theme-mode';
import {ThemeService} from '../services/theme.service';


// provided per component, not root
@Injectable()
export class HeaderController {

    private readonly theme = inject(ThemeService);

    readonly navItems: NavItem[] = [
        {label: 'Home', route: '/home', icon: 'pi pi-home'},
        {label: 'Demo', route: '/demo', icon: 'pi pi-desktop'},
        {label: 'Sign in', route: '/sign-in', icon: 'pi pi-sign-in'}
    ];

    readonly themeOptions = [
        {label: 'System', value: ThemeMode.SYSTEM},
        {label: 'White', value: ThemeMode.LIGHT},
        {label: 'Dark', value: ThemeMode.DARK},
    ];

    readonly menuOpen = signal(false);
    readonly themeMode = this.theme.mode;

    toggleMenu(): void {
        this.menuOpen.update((open) => !open);
    }

    closeMenu(): void {
        this.menuOpen.set(false);
    }

    setTheme(mode: ThemeMode): void {
        this.theme.setMode(mode);
    }

}
