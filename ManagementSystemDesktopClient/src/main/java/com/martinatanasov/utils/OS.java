package com.martinatanasov.utils;

import com.formdev.flatlaf.util.SystemInfo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class OS {

    private OS() {
        /* This utility class should not be instantiated */
    }

    public static boolean isSystemDarkMode() {
        try {
            // Windows 10+
            if (SystemInfo.isWindows) {
                return isWindowsDarkTheme();
            }
            // macOS 10.14+
            if (SystemInfo.isMacOS) {
                return isMacOSDarkTheme();
            }
            // Linux — check GTK theme name or color-scheme
            if (SystemInfo.isLinux) {
                return isLinuxDarkTheme();
            }
        } catch (Exception e) {
            // ignore — fallback to light
            log.error("Failed to determine OS dark mode", e);
        }
        // Default to light
        return false;
    }

    private static boolean isLinuxDarkTheme() throws IOException {
        // Modern desktops (GNOME 42+, KDE via xdg-desktop-portal)
        Process process = Runtime.getRuntime().exec(new String[]{
                "gsettings", "get", "org.gnome.desktop.interface", "color-scheme"
        });
        String output = new String(process.getInputStream().readAllBytes()).trim();
        if (!output.isEmpty()) {
            log.info("Linux (Gnome/Cosmic) Dark Mode: {}", output.contains("dark"));
            return output.contains("dark");
        }
        // Fallback: check gtk-theme name
        process = Runtime.getRuntime().exec(new String[]{
                "gsettings", "get", "org.gnome.desktop.interface", "gtk-theme"
        });
        output = new String(process.getInputStream().readAllBytes()).trim().toLowerCase();

        if (!output.isEmpty()) {
            log.info("Linux (KDE) Dark Mode: {}", output.contains("dark"));
            return output.contains("dark");
        }
        log.info("Linux: Unknown desktop environment, defaulting to light");
        return false;
    }

    private static boolean isMacOSDarkTheme() throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{
                "defaults", "read", "-g", "AppleInterfaceStyle"
        });
        String output = new String(process.getInputStream().readAllBytes()).trim();

        boolean isDarkMode = output.equalsIgnoreCase("Dark");
        log.info("MacOS Dark Mode: {}", isDarkMode);
        return isDarkMode;
    }

    private static boolean isWindowsDarkTheme() throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{
                "reg", "query",
                "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize",
                "/v", "AppsUseLightTheme"
        });
        String output = new String(process.getInputStream().readAllBytes());
        // AppsUseLightTheme = 0x0 means dark mode, 0x1 means light mode
        boolean isDarkMode = output.contains("0x0");
        log.info("Windows Dark Mode: {}", isDarkMode);
        return isDarkMode;
    }

}