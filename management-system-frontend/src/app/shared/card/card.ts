import {Component, input} from '@angular/core';

@Component({
    imports: [],
    selector: 'app-card',
    styleUrl: './card.css',
    templateUrl: './card.html',
    host: {class: 'block w-full max-w-md'},
})
export class Card {
    readonly title = input<string>();
}
