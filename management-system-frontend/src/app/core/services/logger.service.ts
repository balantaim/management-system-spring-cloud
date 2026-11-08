import {Service} from '@angular/core';
import {environment} from "../../../environments/environment";

export type LogLevel = 'debug' | 'info' | 'warn' | 'error';

const ORDER: Record<LogLevel, number> = {debug: 0, info: 1, warn: 2, error: 3};
const SENSITIVE_KEY = /pass(word)?|token|authorization|secret|cookie/i;
const MAX_DEPTH = 4;

@Service()
export class LoggerService {

    /** Production logs warnings and errors only */
    private readonly minLevel: LogLevel = environment.production ? 'warn' : 'debug';

    debug(message: string, ...data: unknown[]): void {
        this.write('debug', message, data);
    }

    info(message: string, ...data: unknown[]): void {
        this.write('info', message, data);
    }

    warn(message: string, ...data: unknown[]): void {
        this.write('warn', message, data);
    }

    error(message: string, ...data: unknown[]): void {
        this.write('error', message, data);
    }

    private write(level: LogLevel, message: string, data: unknown[]): void {
        if (ORDER[level] < ORDER[this.minLevel]) return;
        const line = `[${new Date().toISOString()}] [${level.toUpperCase()}] ${message}`;
        console[level](line, ...data.map((item) => this.sanitize(item)));
    }

    /** Hides passwords, tokens and authorization values before anything reaches the console */
    private sanitize(value: unknown, depth = 0): unknown {
        if (depth > MAX_DEPTH) return '[truncated]';
        if (Array.isArray(value)) return value.map((item) => this.sanitize(item, depth + 1));
        if (value && typeof value === 'object' && Object.getPrototypeOf(value) === Object.prototype) {
            return Object.fromEntries(
                Object.entries(value).map(([key, item]) => [
                    key,
                    SENSITIVE_KEY.test(key) ? '[redacted]' : this.sanitize(item, depth + 1),
                ]),
            );
        }
        return value;
    }

}
