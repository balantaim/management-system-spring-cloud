import {Component, computed, input, signal} from '@angular/core';
import {FieldTree, FormField} from "@angular/forms/signals";
import {FloatLabel} from "@openng/optimus-ui/floatlabel";
import {InputIconModule} from "@openng/optimus-ui/inputicon";
import {InputText} from "@openng/optimus-ui/inputtext";
import {IconFieldModule} from "@openng/optimus-ui/iconfield";
import {UiSize} from "../../core/models/ui-config";

@Component({
    imports: [FormField, FloatLabel, IconFieldModule, InputIconModule, InputText],
    selector: 'app-text-field',
    styleUrl: './text-field.css',
    templateUrl: './text-field.html',
    host: {class: 'block'},
})
export class TextField {

    readonly field = input.required<FieldTree<string>>();
    readonly id = input.required<string>();
    readonly label = input.required<string>();
    readonly type = input<'text' | 'email' | 'password'>('text');
    readonly icon = input<string>();
    readonly autocomplete = input('off');
    // 'small' | 'large' | undefined
    readonly size = input<UiSize>();
    readonly revealable = input(false);
    readonly showMessages = input(true);

    protected readonly visible = signal(false);

    protected readonly inputType = computed(() =>
        this.type() === 'password' && this.visible() ? 'text' : this.type(),
    );

    /** Errors are shown only after the field was touched, so nothing is red on page load */
    protected readonly messages = computed(() => {
        const state = this.field()();
        if (!state.touched()) return [];
        const errors = state.errors();
        const required = errors.find((e) => e.kind === 'required');
        return (required ? [required] : errors).map((e) => e.message ?? '').filter(Boolean);
    });

    protected readonly showError = computed(() => this.messages().length > 0);

    protected toggleVisibility(): void {
        this.visible.update((v) => !v);
    }

}
