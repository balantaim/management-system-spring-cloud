package com.martinatanasov.view;

import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.martinatanasov.uicomponents.Toast;
import com.martinatanasov.view.panels.HomePanel;
import com.martinatanasov.view.panels.LoginPanel;
import com.martinatanasov.view.router.Router;
import com.martinatanasov.view.router.Routes;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Slf4j
@RequiredArgsConstructor
@Component
public class MainFrame extends JFrame implements Theme {

    private final ConfigurableApplicationContext context;
    private final Toast toast;
    private final Environment environment;
    private final GridBagLayout gridBagLayout = new GridBagLayout();
    private final JPanel rootPanel = new JPanel(gridBagLayout);
    private final LoginPanel loginPanel;
    private final HomePanel homePanel;
    private final Router router;

    @PostConstruct
    public void init() {
        setTitle("Management System");
        initFlatInspector();
        setAppTheme(environment.getProperty("app.theme-variant", "light"),
                environment.getProperty("app.theme-name", "Material"));
        setPreferredSize();
        setDefaultMinSize();
        setLocationRelativeTo(null);
        setIconImage(getApplicationIcon());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //Init the Router
        router.init(rootPanel);
        //Register all views inside the Router
        registerViewsInRouter();
        //Load the Login screen
        router.navigateTo(Routes.LOGIN);
        //Add the root container
        add(rootPanel);

        addAppCloseListener();
    }

    private void addAppCloseListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                context.close();
                System.exit(0);
            }
        });
    }

    private void initFlatInspector() {
        if (Boolean.parseBoolean(environment.getProperty("flat.inspector.enabled", "false"))) {
            FlatInspector.install("ctrl shift alt X");
        }
    }

    private void setDefaultMinSize() {
        setMinimumSize(new Dimension(
                Integer.parseInt(environment.getProperty("screen.minimum-resolution.width", "800")),
                Integer.parseInt(environment.getProperty("screen.minimum-resolution.height", "500"))));
    }

    private void setPreferredSize() {
        setSize(Integer.parseInt(environment.getProperty("screen.preferred-resolution.width", "1024")),
                Integer.parseInt(environment.getProperty("screen.preferred-resolution.height", "800")));
    }

    private void registerViewsInRouter() {
        router.register(Routes.LOGIN, loginPanel.getView());
        router.register(Routes.HOME, homePanel.getView());
    }

    private Image getApplicationIcon() {
        FlatSVGIcon appIcon = new FlatSVGIcon("static/images/app-logo.svg", 64, 64);
        appIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.decode("#5B2DA3")));
        return appIcon.getImage();
    }

}
