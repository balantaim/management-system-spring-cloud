package com.martinatanasov.uicomponents.toast;

import lombok.Getter;

import java.awt.*;

@Getter
public enum ToastType {

    SUCCESS(new Color(76, 175, 80), "static/images/success-36.svg"),
    ERROR(new Color(244, 67, 54), "static/images/error-36.svg"),
    INFO(new Color(33, 150, 243), "static/images/info-36.svg"),
    WARNING(new Color(255, 152, 0), "static/images/warning-36.svg");

    private final Color color;
    private final String iconPath;

    ToastType(Color color, String iconPath) {
        this.color = color;
        this.iconPath = iconPath;
    }

}
