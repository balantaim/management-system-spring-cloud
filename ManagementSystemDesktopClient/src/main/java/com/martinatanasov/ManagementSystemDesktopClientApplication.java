package com.martinatanasov;

import com.martinatanasov.view.MainFrame;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class ManagementSystemDesktopClientApplication {

    public static void main(String[] args) {

        // Enable GPU acceleration BEFORE Swing initializes
//        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.d3d", "true");
        System.setProperty("sun.java2d.metal", "true");

        // Start Spring
        ConfigurableApplicationContext context = new SpringApplicationBuilder(ManagementSystemDesktopClientApplication.class)
                //Enable UI
                .headless(false)
                .run(args);

        // Start Swing UI on EDT
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = context.getBean(MainFrame.class);
            frame.setVisible(true);
        });
    }

}

