import {Component, inject} from '@angular/core';
import {Location} from '@angular/common';
import {RouterLink} from "@angular/router";
import {Card} from "../../shared/card/card";

@Component({
    imports: [RouterLink, Card],
    selector: 'app-not-found',
    styleUrl: './not-found.css',
    templateUrl: './not-found.html',
})
export class NotFound {

    private readonly location = inject(Location);

    protected goBack(): void {
        this.location.back();
    }
}
