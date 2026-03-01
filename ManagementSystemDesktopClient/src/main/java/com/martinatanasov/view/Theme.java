package com.martinatanasov.view;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.*;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDarkerIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDeepOceanIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialLighterIJTheme;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.formdev.flatlaf.util.SystemInfo;

import javax.swing.*;

public interface Theme {

    default void setAppTheme(String themeVariant, String themeName) {
        if (themeVariant.equals("light")) {
            switch (themeName) {
                case "macOS" -> FlatMacLightLaf.setup();
                case "IntelliJ" -> FlatIntelliJLaf.setup();
                case "Cyan-Purple" -> FlatCyanLightIJTheme.setup();
                case "Material" -> FlatMTMaterialLighterIJTheme.setup();
                case "Solarized-Carbon" -> FlatSolarizedDarkIJTheme.setup();
                case "Orange-Ocean" -> FlatArcOrangeIJTheme.setup();
                default -> FlatLightLaf.setup();
            }
        } else {
            switch (themeName) {
                case "macOS" -> FlatMacDarkLaf.setup();
                case "IntelliJ" -> FlatDarculaLaf.setup();
                case "Cyan-Purple" -> FlatDarkPurpleIJTheme.setup();
                case "Material" -> FlatMTMaterialDarkerIJTheme.setup();
                case "Solarized-Carbon" -> FlatCarbonIJTheme.setup();
                case "Orange-Ocean" -> FlatMTMaterialDeepOceanIJTheme.setup();
                default -> FlatDarkLaf.setup();
            }
        }
    }

    default void enableDecorations(Boolean isEnabledForLinux) {
        if (SystemInfo.isLinux && isEnabledForLinux) {
            // enable custom window decorations
            JFrame.setDefaultLookAndFeelDecorated(true);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }
    }

}
