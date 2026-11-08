import {HttpContextToken} from "@angular/common/http";

/** Public requests (login, refresh): no Bearer header, no token refresh, no global error toast */
export const PUBLIC_REQUEST = new HttpContextToken<boolean>(() => false);

/** Set on the repeated request after a token refresh, so a request is retried only once */
export const IS_RETRY = new HttpContextToken<boolean>(() => false);
