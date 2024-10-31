package org.swdc.xswing.view;

import org.swdc.dependency.DependencyContext;
import org.swdc.swing.Component;

import javax.swing.*;
import java.awt.*;

public abstract class AbstractSwingView {

    private Window window;

    private Component component;

    private ViewController controller;

    private DependencyContext context;


    void setContext(DependencyContext context) {
        this.context = context;
    }

    public <T extends AbstractSwingView> T getView(Class<T> view) {
        return context.getByClass(view);
    }

    public JComponent getView() {
        if (component != null) {
            return component.getComponent();
        }
        component = createView();
        return component.getComponent();
    }

    public Component getComponent() {
        if (component != null) {
            return component;
        }
        component = createView();
        return component;
    }

    void setController(ViewController controller) {
        if (this.controller != null) {
            throw new RuntimeException("can not set a new controller , the view already has a controller");
        }
        this.controller = controller;
        this.controller.setView(this);
        component.controller(controller);
    }

    public void show() {
        if (window.isShowing()) {
            window.toFront();
        } else {
            if (window instanceof JDialog) {
                JDialog dialog = (JDialog) window;
                dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            }
            window.setVisible(true);
        }
    }

    public ViewController getController() {
        return controller;
    }

    protected abstract Component createView();

    public void setDisable(JComponent component, boolean val) {
        for(java.awt.Component it : component.getComponents()) {
            it.setEnabled(!val);
            if (it instanceof JComponent) {
                JComponent container = (JComponent) it;
                setDisable(container,val);
            }
        }
    }

    void setWindow(Window frame) {
        this.window = frame;
    }

    public JFrame getFrame() {
        if (window instanceof JFrame) {
            return (JFrame) window;
        }
        throw new RuntimeException("this is not a JFrame view.");
    }

    public JDialog getDialog() {
        if (window instanceof JDialog) {
            return (JDialog) window;
        }
        return null;
    }



}
