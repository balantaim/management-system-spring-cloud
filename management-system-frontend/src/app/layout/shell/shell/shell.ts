import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Header} from '../../header/header/header';
import {Footer} from '../../footer/footer/footer';


@Component({
    imports: [RouterOutlet, Header, Footer],
    selector: 'app-shell',
    styleUrl: './shell.css',
    templateUrl: './shell.html',
})
export class Shell {
}
