package com.martinatanasov.view;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDarkerIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDeepOceanIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialLighterIJTheme;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.SystemInfo;

import javax.swing.*;
import java.awt.*;

public interface Theme {

    default void setAppTheme(String themeVariant, String themeName) {
        if (themeVariant.equalsIgnoreCase("light")) {
            switch (themeName.toLowerCase()) {
                case "macos" -> FlatMacLightLaf.setup();
                case "intellij" -> FlatIntelliJLaf.setup();
                case "cyan-purple" -> FlatCyanLightIJTheme.setup();
                case "material" -> FlatMTMaterialLighterIJTheme.setup();
                case "solarized-carbon" -> FlatSolarizedDarkIJTheme.setup();
                case "orange-ocean" -> FlatArcOrangeIJTheme.setup();
                default -> FlatLightLaf.setup();
            }
        } else {
            switch (themeName.toLowerCase()) {
                case "macos" -> FlatMacDarkLaf.setup();
                case "intellij" -> FlatDarculaLaf.setup();
                case "cyan-purple" -> FlatDarkPurpleIJTheme.setup();
                case "material" -> FlatMTMaterialDarkerIJTheme.setup();
                case "solarized-carbon" -> FlatCarbonIJTheme.setup();
                case "orange-ocean" -> FlatMTMaterialDeepOceanIJTheme.setup();
                default -> FlatDarkLaf.setup();
            }
        }
    }

    default void enableDecorations(boolean isEnabledForLinux) {
        if (SystemInfo.isLinux && isEnabledForLinux) {
            // enable custom window decorations
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
    }

    default Color getErrorColor() {
        return UIManager.getColor("Component.error.focusedBorderColor");
    }

    default Color getLabelColor() {
        return UIManager.getColor("Label.foreground");
    }

    default Color getTextFieldColor() {
        return UIManager.getColor("TextField.foreground");
    }

    default Color getAccentColor() {
        return UIManager.getColor("Component.accentColor");
    }

    default FlatSVGIcon.ColorFilter getColorFilter(Color newColor) {
        return new FlatSVGIcon.ColorFilter(color -> newColor);
    }


}
