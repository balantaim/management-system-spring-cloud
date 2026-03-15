package com.martinatanasov.animations.transition;

public class TransitionFactory {

    public static PanelTransition getTransition(TransitionType type) {
        return switch (type) {
            case CROSSFADE -> new CrossfadeTransition(700);
            case SLIDE_LEFT -> new SlideTransition(-1, 0);
            case SLIDE_RIGHT -> new SlideTransition(1, 0);
            case SLIDE_UP -> new SlideTransition(0, -1);
            case SLIDE_DOWN -> new SlideTransition(0, 1);
            case NONE -> null;
        };
    }

}