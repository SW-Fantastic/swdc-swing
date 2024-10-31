package org.swdc.xswing;

public abstract class Splash {

    protected SwingResource resources;

    public Splash(SwingResource resources) {
        this.resources = resources;
    }

    public abstract void show();

    public abstract void hide();

}
