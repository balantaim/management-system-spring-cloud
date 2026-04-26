package com.martinatanasov.view;

import com.formdev.flatlaf.extras.FlatInspector;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.martinatanasov.requests.AsyncExecutor;
import com.martinatanasov.view.panels.HomePanel;
import com.martinatanasov.view.panels.LoginPanel;
import com.martinatanasov.view.panels.RegisterPanel;
import com.martinatanasov.view.router.Router;
import com.martinatanasov.view.router.Routes;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.Environment;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Slf4j
@Singleton
public class MainFrame extends JFrame implements Theme {

    private final ApplicationContext context;
    private final Environment environment;
    //Centered layout
    private final GridBagLayout gridBagLayout = new GridBagLayout();
    private final JPanel rootPanel = new JPanel(gridBagLayout);
    private final LoginPanel loginPanel;
    private final RegisterPanel registerPanel;
    private final HomePanel homePanel;
    private final Router router;

    public MainFrame(ApplicationContext context,
            Environment environment,
            LoginPanel loginPanel,
            RegisterPanel registerPanel,
            HomePanel homePanel,
            Router router) {
        this.context = context;
        this.environment = environment;
        this.loginPanel = loginPanel;
        this.registerPanel = registerPanel;
        this.homePanel = homePanel;
        this.router = router;
    }

    public void init() {
        setTitle(environment.getProperty("app.title", String.class).orElse("Management System"));
        setName("main-frame");
        initFlatInspector();
        setAppTheme(
                environment.getProperty("app.theme-variant", String.class).orElse("light"),
                environment.getProperty("app.theme-name", String.class).orElse("Material")
        );
        setPreferredSize();
        setDefaultMinSize();
        setLocationRelativeTo(null);
        setIconImage(getApplicationIcon());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        //Init the Router
        router.init(this, loginPanel.getView());
        //Register all views inside the Router
        registerViewsInRouter();
        //Load the Login screen
        router.navigateTo(Routes.LOGIN);
        //Add the root container
        add(rootPanel);
        setVisible(true);
        addAppCloseListener();
    }

    public JPanel getRoot() {
        return rootPanel;
    }

    private void addAppCloseListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                AsyncExecutor.shutdown();
                context.close();
                System.exit(0);
            }
        });
    }

    private void initFlatInspector() {
        boolean enabled = environment.getProperty("flat.inspector.enabled", Boolean.class)
                .orElse(false);
        if (enabled) {
            FlatInspector.install("ctrl shift alt X");
        }
    }

    private void setDefaultMinSize() {
        int width = environment.getProperty("screen.minimum-resolution.width", Integer.class)
                .orElse(800);
        int height = environment.getProperty("screen.minimum-resolution.height", Integer.class)
                .orElse(500);
        setMinimumSize(new Dimension(width, height));
    }

    private void setPreferredSize() {
        int width = environment.getProperty("screen.preferred-resolution.width", Integer.class)
                .orElse(1024);
        int height = environment.getProperty("screen.preferred-resolution.height", Integer.class)
                .orElse(800);
        setSize(width, height);
    }

    private void registerViewsInRouter() {
        router.registerRoutes(Routes.LOGIN, loginPanel.getView());
        router.registerRoutes(Routes.REGISTER, registerPanel.getView());
        router.registerRoutes(Routes.HOME, homePanel.getView());
    }

    private Image getApplicationIcon() {
        FlatSVGIcon appIcon = new FlatSVGIcon("static/images/app-logo-36.svg", 64, 64);
        Color focusColor = UIManager.getColor("Component.focusColor");
        appIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> focusColor));
        return appIcon.getImage();
    }
}
