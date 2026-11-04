import {Component, inject} from '@angular/core';
import {RegisterController} from "../../core/controllers/register.controller";
import {NgComponentOutlet} from "@angular/common";
import {RouterLink} from "@angular/router";
import {Card} from "../../shared/card/card";

@Component({
    imports: [NgComponentOutlet, RouterLink, Card],
    providers: [RegisterController],
    selector: 'app-register',
    styleUrl: './register.css',
    templateUrl: './register.html',
})
export class Register {
    protected readonly ctrl = inject(RegisterController);
}
