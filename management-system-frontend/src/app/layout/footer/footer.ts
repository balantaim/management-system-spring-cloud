import {Component, inject} from '@angular/core';
import {RouterLink} from '@angular/router';
import {AuthService} from "../../core/services/auth.service";

@Component({
    imports: [RouterLink],
    selector: 'app-footer',
    styleUrl: './footer.css',
    templateUrl: './footer.html',
})
export class Footer {
    protected readonly auth = inject(AuthService);
    protected readonly year = new Date().getFullYear();
}
