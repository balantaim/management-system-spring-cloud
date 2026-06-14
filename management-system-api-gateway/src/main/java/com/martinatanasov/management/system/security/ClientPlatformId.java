package com.martinatanasov.management.system.security;

import lombok.Getter;

@Getter
public enum ClientPlatformId {
    WEB("1000"),
    DESKTOP("1001"),
    MOBILE("1002");

    private final String platformId;

    ClientPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public static boolean isValid(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        for (ClientPlatformId platform : values()) {
            if (platform.platformId.equals(value.trim())) {
                return true;
            }
        }
        return false;
    }
}
