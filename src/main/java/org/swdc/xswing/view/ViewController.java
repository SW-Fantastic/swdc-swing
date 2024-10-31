package org.swdc.xswing.view;

public class ViewController<T extends AbstractSwingView> {

    private T view;

    protected void initialize() {

    }

    void doInit() {
        this.initialize();
    }

    public T getView() {
        return view;
    }

    void setView(T view) {
        this.view = view;
    }
}
