// import { Component } from '@angular/core';
//
// @Component({
//   imports: [],
//   selector: 'app-header',
//   styleUrl: './header.css',
//   templateUrl: './header.html',
// })
// export class Header {}

import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {Select} from '@openng/optimus-ui/select';
import {HeaderController} from '../../../core/controllers/header.controller';


@Component({
    selector: 'app-header',
    imports: [RouterLink, RouterLinkActive, FormsModule, Select],
    providers: [HeaderController],
    templateUrl: './header.html',
})
export class Header {
    protected readonly ctrl = inject(HeaderController);
}
