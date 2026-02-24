package com.martinatanasov.uicomponents;

import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class Toast {

    public void showToast(final String message, final Frame frame) {
        JDialog toast = new JDialog(frame);
        toast.setUndecorated(true);
        toast.setSize(200, 60);
        toast.setLayout(new BorderLayout(45, 45));

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        toast.add(label, BorderLayout.CENTER);

        toast.setLocationRelativeTo(frame);
        toast.setVisible(true);

        new Timer(3000, e -> toast.dispose()).start();
    }

}
