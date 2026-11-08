export function decodeJwtPayload(token: string): Record<string, unknown> | null {
    const part = token.split('.')[1];
    if (!part) return null;

    try {
        const base64 = part.replace(/-/g, '+').replace(/_/g, '/');
        const padded = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
        const json = new TextDecoder().decode(Uint8Array.from(atob(padded), (char) => char.charCodeAt(0)));
        return JSON.parse(json) as Record<string, unknown>;
    } catch {
        return null;
    }
}

/** A token without a readable `exp` counts as expired, so the app tries a refresh */
export function isTokenExpired(token: string, skewSeconds = 30): boolean {
    const exp = decodeJwtPayload(token)?.['exp'];
    if (typeof exp !== 'number') return true;
    return exp * 1000 - skewSeconds * 1000 <= Date.now();
}

/** Reads the "authorities" claim, e.g. ["ROLE_CUSTOMER", "ADMIN_READ"] */
export function readAuthorities(token: string): string[] {
    const value = decodeJwtPayload(token)?.['authorities'];
    return Array.isArray(value) ? value.filter((item): item is string => typeof item === 'string') : [];
}
