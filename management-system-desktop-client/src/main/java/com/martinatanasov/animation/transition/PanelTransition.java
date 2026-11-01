package com.martinatanasov.animation.transition;

import javax.swing.*;

public interface PanelTransition {

    void animate(JPanel root, JPanel oldPanel, JPanel newPanel, Runnable onComplete);

}
