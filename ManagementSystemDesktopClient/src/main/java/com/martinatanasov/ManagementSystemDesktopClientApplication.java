package com.martinatanasov;

import com.martinatanasov.view.MainFrame;
import io.micronaut.context.ApplicationContext;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.Map;

@Slf4j
public class ManagementSystemDesktopClientApplication {

    public static void main(String[] args) {
        // Init the default theme and font BEFORE anything else
        //new DefaultThemeAndFont();

        System.setProperty("flatlaf.useSystemTheme", "true");
        System.setProperty("flatlaf.useWindowDecorations", "true");
        // Enable UI scale
        System.setProperty("sun.java2d.uiScale.enabled", "true");
        System.setProperty("sun.java2d.uiScale", "1x");
        System.setProperty("flatlaf.uiScale", "1x");
        // Enable GPU acceleration BEFORE Swing initializes
        System.setProperty("sun.java2d.d3d", "true");
        System.setProperty("sun.java2d.metal", "true");

        // Build Micronaut context — no web server, headless=false for Swing
        ApplicationContext context = ApplicationContext.builder()
                .args(args)
                // No banner
                .banner(false)
//                .environments("prod")
                .deduceEnvironment(false)
                .properties(Map.of(
                        // no HTTP server
                        "micronaut.server.port", "-1",
                        // required for Swing
                        "java.awt.headless", "false"
                ))
                .start();

        // Start Swing UI on EDT
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = context.getBean(MainFrame.class);
            frame.init();
        });

        // Close context when Swing exits
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down application context");
            context.close();
        }));
    }

}

