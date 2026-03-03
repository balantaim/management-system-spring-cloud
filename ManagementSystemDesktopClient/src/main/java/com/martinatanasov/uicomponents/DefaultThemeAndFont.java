package com.martinatanasov.uicomponents;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.fonts.roboto.FlatRobotoFont;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMTMaterialDarkerIJTheme;

import javax.swing.*;
import java.awt.*;

public class DefaultThemeAndFont {

    public DefaultThemeAndFont() {
        init();
    }

    public void init() {
        //Install Roboto font
        FlatRobotoFont.install();
        //Register theme files from resources dir(separate directories with "." instead "/")
        FlatLaf.registerCustomDefaultsSource("themes");
        //Setup initial theme
        FlatMTMaterialDarkerIJTheme.setup();
        //Set global app font
        UIManager.put("defaultFont", new Font(FlatRobotoFont.FAMILY, Font.PLAIN, 15));
    }

}
