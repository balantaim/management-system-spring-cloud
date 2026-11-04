export const EMAIL_MAX_LENGTH = 255;
export const PASSWORD_MIN_LENGTH = 8;
export const PASSWORD_MAX_LENGTH = 50;

export const HAS_UPPERCASE = /[A-Z]/;
export const HAS_LOWERCASE = /[a-z]/;
export const HAS_NUMBER = /\d/;
export const HAS_SPECIAL = /[^A-Za-z0-9\s]/;

export const FULL_NAME_MIN_LENGTH = 3;
export const FULL_NAME_MAX_LENGTH = 150;

/** Unicode letters, spaces, hyphens and apostrophes; must start with a letter */
export const FULL_NAME_PATTERN = /^\p{L}[\p{L} '’-]*$/u;

export interface PasswordRule {
    label: string;
    test: (value: string) => boolean;
}

/** One list drives the strength meter and the checklist */
export const PASSWORD_RULES: PasswordRule[] = [
    {label: `At least ${PASSWORD_MIN_LENGTH} characters`, test: (v) => v.length >= PASSWORD_MIN_LENGTH},
    {label: '1 uppercase letter', test: (v) => HAS_UPPERCASE.test(v)},
    {label: '1 lowercase letter', test: (v) => HAS_LOWERCASE.test(v)},
    {label: '1 number', test: (v) => HAS_NUMBER.test(v)},
    {label: '1 special character', test: (v) => HAS_SPECIAL.test(v)},
];
