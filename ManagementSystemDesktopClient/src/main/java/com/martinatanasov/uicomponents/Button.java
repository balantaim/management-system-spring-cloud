package com.martinatanasov.uicomponents;

import org.springframework.stereotype.Component;

import javax.swing.*;

@Component
public class Button {

    public JButton getButton(String text, String id) {
        return new JButton(text);
    }

}
