import {DOCUMENT} from '@angular/common';
import {computed, effect, inject, signal, Service} from '@angular/core';
import {ThemeMode} from '../models/theme-mode';

const THEME_STORAGE_KEY = 'theme-mode';

@Service()
export class ThemeService {

    private readonly document = inject(DOCUMENT);
    private readonly media = this.document.defaultView?.matchMedia('(prefers-color-scheme: dark)');
    private readonly systemDark = signal(this.media?.matches ?? false);
    readonly mode = signal<ThemeMode>(this.readStored());

    readonly isDark = computed(
        () => this.mode() === ThemeMode.DARK || (this.mode() === ThemeMode.SYSTEM && this.systemDark()),
    );

    constructor() {
        // follow OS changes while "system" is selected
        this.media?.addEventListener('change', (e) => this.systemDark.set(e.matches));

        // the single place that touches the DOM
        effect(() => {
            this.document.documentElement.classList.toggle('app-dark', this.isDark());
        });
    }

    setMode(mode: ThemeMode): void {
        this.mode.set(mode);
        try {
            localStorage.setItem(THEME_STORAGE_KEY, mode);
        } catch {
        }
    }

    private readStored(): ThemeMode {
        try {
            const value = localStorage.getItem(THEME_STORAGE_KEY);
            if (Object.values(ThemeMode).includes(value as ThemeMode)) return value as ThemeMode;
        } catch {
        }
        return ThemeMode.SYSTEM;
    }

}