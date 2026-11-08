export interface TokenResponse {
    access_token: string;
    refresh_token: string;
}

/** The refresh endpoint may or may not rotate the refresh token */
export interface RefreshResponse {
    access_token: string;
    refresh_token?: string;
}
