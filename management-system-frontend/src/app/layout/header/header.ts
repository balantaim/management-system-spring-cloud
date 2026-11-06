import {Component, ElementRef, inject, viewChild} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterLink, RouterLinkActive} from '@angular/router';
import {Select} from '@openng/optimus-ui/select';
import {HeaderController} from '../../core/controllers/header.controller';
import {Dropdown} from "../../shared/dropdown/dropdown";


@Component({
    selector: 'app-header',
    imports: [RouterLink, RouterLinkActive, Dropdown],
    providers: [HeaderController],
    templateUrl: './header.html',
    host: {
        '(document:click)': 'onDocumentClick($event)',
        '(document:keydown.escape)': 'ctrl.closeUserMenu()'
    }
})
export class Header {

    protected readonly ctrl = inject(HeaderController);
    private readonly userMenu = viewChild<ElementRef<HTMLElement>>('userMenu');

    /** Clicking anywhere outside the user menu closes it */
    protected onDocumentClick(event: MouseEvent): void {
        const element = this.userMenu()?.nativeElement;
        if (element && !element.contains(event.target as Node)) {
            this.ctrl.closeUserMenu();
        }
    }

}
