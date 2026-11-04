import {Component, input} from '@angular/core';
import {UiSize} from "../../core/models/ui-config";
import {Button} from "@openng/optimus-ui/button";

@Component({
    imports: [Button],
    selector: 'app-submit-button',
    styleUrl: './submit-button.css',
    templateUrl: './submit-button.html',
    host: {class: 'block'},
})
export class SubmitButton {
    readonly label = input.required<string>();
    readonly loading = input(false);
    readonly disabled = input(false);
    readonly size = input<UiSize>();
    readonly fluid = input(true);
}
