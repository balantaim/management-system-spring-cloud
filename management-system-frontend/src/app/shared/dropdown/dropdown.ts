import {
    Component,
    ElementRef,
    Injector,
    afterNextRender,
    computed,
    inject,
    input,
    model,
    signal,
    viewChild,
} from '@angular/core';
import {DropdownOption} from "../../core/models/dropdown-option";


let nextId = 0;

@Component({
    imports: [],
    selector: 'app-dropdown',
    styleUrl: './dropdown.css',
    templateUrl: './dropdown.html',
    host: {
        class: 'relative inline-block',
        '(document:click)': 'onDocumentClick($event)',
    }
})
export class Dropdown<T = string> {
    private readonly host = inject<ElementRef<HTMLElement>>(ElementRef);
    private readonly injector = inject(Injector);

    readonly options = input.required<DropdownOption<T>[]>();
    readonly value = model.required<T>();             // supports [(value)]
    readonly ariaLabel = input('Select');

    protected readonly uid = `dropdown-${nextId++}`;
    protected readonly open = signal(false);
    protected readonly activeIndex = signal(0);

    private readonly trigger = viewChild<ElementRef<HTMLButtonElement>>('trigger');
    private readonly list = viewChild<ElementRef<HTMLUListElement>>('list');

    protected readonly selected = computed(() =>
        this.options().find((option) => option.value === this.value()),
    );

    protected isSelected(option: DropdownOption<T>): boolean {
        return option.value === this.value();
    }

    protected toggle(): void {
        if (this.open()) {
            this.close(false);
        } else {
            this.openList();
        }
    }

    protected select(option: DropdownOption<T>): void {
        this.value.set(option.value);
        this.close(true);
    }

    protected onTriggerKeydown(event: KeyboardEvent): void {
        if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
            event.preventDefault();
            if (!this.open()) this.openList();
        }
    }

    protected onListKeydown(event: KeyboardEvent): void {
        const last = this.options().length - 1;

        switch (event.key) {
            case 'ArrowDown':
                this.activeIndex.update((i) => Math.min(i + 1, last));
                break;
            case 'ArrowUp':
                this.activeIndex.update((i) => Math.max(i - 1, 0));
                break;
            case 'Home':
                this.activeIndex.set(0);
                break;
            case 'End':
                this.activeIndex.set(last);
                break;
            case 'Enter':
            case ' ': {
                const option = this.options()[this.activeIndex()];
                if (option) this.select(option);
                break;
            }
            case 'Escape':
                this.close(true);
                break;
            case 'Tab':
                this.close(false);
                return;
            default:
                return;
        }
        event.preventDefault();
    }

    /** Clicking anywhere outside the dropdown closes it */
    protected onDocumentClick(event: MouseEvent): void {
        if (this.open() && !this.host.nativeElement.contains(event.target as Node)) {
            this.close(false);
        }
    }

    private openList(): void {
        const index = this.options().findIndex((option) => option.value === this.value());
        this.activeIndex.set(Math.max(index, 0));
        this.open.set(true);
        // move focus into the list once it is rendered, so the arrow keys work
        afterNextRender(() => this.list()?.nativeElement.focus(), {injector: this.injector});
    }

    private close(restoreFocus: boolean): void {
        this.open.set(false);
        if (restoreFocus) this.trigger()?.nativeElement.focus();
    }
}
