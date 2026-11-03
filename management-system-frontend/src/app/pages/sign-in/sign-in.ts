import {Component, inject} from '@angular/core';
import {SignInController} from "../../core/controllers/sign-in.controller";
import {RouterLink} from "@angular/router";
import {NgComponentOutlet} from "@angular/common";
import {Card} from "../../shared/card/card";


@Component({
    imports: [NgComponentOutlet, RouterLink, Card],
    selector: 'app-sign-in',
    styleUrl: './sign-in.css',
    providers: [SignInController],
    templateUrl: './sign-in.html',
})
export class SignIn {
    protected readonly ctrl = inject(SignInController);
}
