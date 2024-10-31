package org.swdc.xswing;

import javax.swing.*;

public abstract class SwingSplash  extends Splash {

    public SwingSplash(SwingResource resources) {
        super(resources);
    }


    public abstract JWindow getSplash();

    @Override
    public void show() {
        getSplash().setVisible(true);
    }

    @Override
    public void hide() {
        getSplash().setVisible(false);
    }

}
