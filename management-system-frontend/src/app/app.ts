import {Component, signal} from '@angular/core';
import {Shell} from './layout/shell/shell/shell';

@Component({
    selector: 'app-root',
    imports: [Shell],
    templateUrl: './app.html',
    styleUrl: './app.css'
})
export class App {
    protected readonly title = signal('management-system-frontend');
}

