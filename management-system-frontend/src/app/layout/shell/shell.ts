import {Component} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Header} from '../header/header';
import {Footer} from '../footer/footer';
import {Toast} from '@openng/optimus-ui/toast';


@Component({
    imports: [RouterOutlet, Header, Footer, Toast],
    selector: 'app-shell',
    styleUrl: './shell.css',
    templateUrl: './shell.html',
})
export class Shell {
}
