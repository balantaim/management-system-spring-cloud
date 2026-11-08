import {inject, Service} from '@angular/core';
import {MessageService} from "@openng/optimus-ui/api";


type Severity = 'success' | 'info' | 'warn' | 'error';
const LIFE_MS = 5000;

@Service()
export class NotificationService {

    private readonly messages = inject(MessageService);

    success(summary: string, detail?: string): void {
        this.show('success', summary, detail);
    }

    info(summary: string, detail?: string): void {
        this.show('info', summary, detail);
    }

    warn(summary: string, detail?: string): void {
        this.show('warn', summary, detail);
    }

    error(summary: string, detail?: string): void {
        this.show('error', summary, detail);
    }

    private show(severity: Severity, summary: string, detail?: string): void {
        this.messages.add({severity, summary, detail, life: LIFE_MS});
    }

}
