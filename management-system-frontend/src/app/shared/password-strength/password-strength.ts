import {Component, computed, input} from '@angular/core';
import {FieldTree} from "@angular/forms/signals";
import {PASSWORD_RULES} from "../../core/validation/auth-rules";


// Maps "rules passed" (1-5) to a level: 1-2 Weak, 3 Fair, 4 Good, 5 Strong
const LEVELS = [
    {label: 'Weak', segments: 1, color: 'bg-red-500'},
    {label: 'Fair', segments: 2, color: 'bg-orange-500'},
    {label: 'Good', segments: 3, color: 'bg-yellow-500'},
    {label: 'Strong', segments: 4, color: 'bg-green-500'},
];

@Component({
    imports: [],
    selector: 'app-password-strength',
    styleUrl: './password-strength.css',
    templateUrl: './password-strength.html',
    host: {class: 'block'},
})
export class PasswordStrength {
    readonly field = input.required<FieldTree<string>>();

    protected readonly segments = [1, 2, 3, 4];

    protected readonly rules = computed(() => {
        const value = this.field()().value();
        return PASSWORD_RULES.map((rule) => ({label: rule.label, passed: rule.test(value)}));
    });

    protected readonly score = computed(() => this.rules().filter((rule) => rule.passed).length);

    protected readonly level = computed(() => {
        const score = this.score();
        if (score === 0) return undefined;
        return score <= 2 ? LEVELS[0] : LEVELS[score - 2];
    });

    protected readonly filled = computed(() => this.level()?.segments ?? 0);
    protected readonly color = computed(() => this.level()?.color ?? '');
}
