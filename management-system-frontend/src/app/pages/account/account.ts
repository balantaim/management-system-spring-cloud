import {Component, inject} from '@angular/core';
import {AuthService} from "../../core/services/auth.service";
import {Card} from "../../shared/card/card";

@Component({
    imports: [Card],
    selector: 'app-account',
    styleUrl: './account.css',
    templateUrl: './account.html',
})
export class Account {
    protected readonly user = inject(AuthService).user;
}
