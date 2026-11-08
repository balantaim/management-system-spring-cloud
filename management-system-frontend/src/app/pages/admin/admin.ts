import {Component, inject} from '@angular/core';
import {Card} from "../../shared/card/card";
import {AuthService} from "../../core/services/auth.service";

@Component({
    imports: [Card],
    selector: 'app-admin',
    styleUrl: './admin.css',
    templateUrl: './admin.html',
})
export class Admin {
    protected readonly authorities = inject(AuthService).authorities;
}
