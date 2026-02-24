package com.martinatanasov.view;

import com.formdev.flatlaf.extras.FlatInspector;
import com.martinatanasov.UserService;
import com.martinatanasov.uicomponents.Toast;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@Component
public class HomeFrame extends JFrame implements Theme {

    private final UserService userService;
    private final ConfigurableApplicationContext context;
    private final Toast toast;
    private final Environment environment;
    private final LoginPanel loginPanel;
    private final ResourceLoader resourceLoader;

    @PostConstruct
    public void init() throws IOException {
        setAppTheme(environment.getProperty("app.theme-variant", "light"),
                environment.getProperty("app.theme-name", "Material"));
        enableFlatInspector();

        setTitle("Management System - Login form");
        setSize(Integer.parseInt(environment.getProperty("screen.preferred-resolution.width", "1024")),
                Integer.parseInt(environment.getProperty("screen.preferred-resolution.height", "800")));
        setMinimumSize(new Dimension(Integer.parseInt(environment.getProperty("screen.minimum-resolution.width", "800")),
                Integer.parseInt(environment.getProperty("screen.minimum-resolution.height", "500"))));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        //Set App icon
//        Resource resource = resourceLoader.getResource("classpath:static/m.png");
        URL url = getClass().getClassLoader().getResource("static/m.png");
        //Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("static/m.png"));
        assert url != null;
        ImageIcon icon = new ImageIcon(url);
        setIconImage(icon.getImage());


        // Main container (center everything)
        JPanel rootPanel = new JPanel(new GridBagLayout());

        rootPanel.add(loginPanel.getLoginView());
        add(rootPanel);

        // Close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                context.close();
                System.exit(0);
            }
        });
    }

    private void enableFlatInspector() {
        if (Boolean.parseBoolean(environment.getProperty("flat.inspector.enabled", "false"))) {
            FlatInspector.install("ctrl shift alt X");
        }
    }

}
