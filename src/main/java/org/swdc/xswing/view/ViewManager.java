package org.swdc.xswing.view;

import org.swdc.dependency.AbstractDependencyScope;
import org.swdc.ours.common.annotations.AnnotationDescription;
import org.swdc.ours.common.annotations.Annotations;

import javax.swing.*;
import java.awt.*;

public class ViewManager extends AbstractDependencyScope {

    @Override
    public Class getScopeType() {
        return XSwingView.class;
    }

    @Override
    public <T> T put(String name, Class clazz, T component) {

        if (!(component instanceof AbstractSwingView)) {
            throw new RuntimeException("please extend AbstractSwingView : init view failed - " + clazz.getName());
        }

        AnnotationDescription description = Annotations.findAnnotation(clazz, XSwingView.class);

        AbstractSwingView swingView = (AbstractSwingView) component;
        JComponent content = swingView.getView();
        if (description.getProperty(Boolean.class,"dialog")) {
            swingView.setWindow(initDialog(description,content));
        } else {
            swingView.setWindow(initFrame(description,content));
        }

        Class controller = description.getProperty(Class.class,"controller");
        if (controller != ViewController.class) {
            if (!ViewController.class.isAssignableFrom(controller)) {
                throw new RuntimeException("to create a view controller please extend ViewController class");
            }
            ViewController controllerInstance = (ViewController) context.getByClass(controller);
            swingView.setController(controllerInstance);
            controllerInstance.initialize();
        }

        swingView.setContext(context);

        boolean multiple = description.getProperty(Boolean.class,"multiple");
        if (multiple) {
            return (T)swingView;
        }
        return super.put(name, clazz, component);
    }

    private JFrame initFrame(AnnotationDescription description, JComponent content) {
        JFrame frame = new JFrame();
        frame.setSize(
                description.getProperty(Integer.class,"width"),
                description.getProperty(Integer.class, "height")
        );
        frame.setTitle(description.getProperty(String.class,"title"));
        frame.setContentPane(content);
        frame.setLocationRelativeTo(null);

        int maxWidth = description.getProperty(Integer.class,"maxWidth");
        int maxHeight = description.getProperty(Integer.class,"maxHeight");

        Dimension max = frame.getMaximumSize();
        if (maxWidth > 0) {
            max.setSize(maxWidth,max.getHeight());
        }

        if (maxHeight > 0) {
            max.setSize(max.getWidth(),maxHeight);
        }
        frame.setMaximumSize(max);

        int minWidth = description.getProperty(Integer.class,"minWidth");
        int minHeight = description.getProperty(Integer.class,"minHeight");
        boolean resize = description.getProperty(Boolean.class, "resizeable");

        Dimension min = frame.getMinimumSize();
        if (minHeight > 0) {
            min.setSize(min.getWidth(),minHeight);
        }
        if (minWidth > 0) {
            min.setSize(minWidth, min.getHeight());
        }
        frame.setMinimumSize(min);
        frame.setResizable(resize);

        return frame;
    }

    private JDialog initDialog(AnnotationDescription description, JComponent content) {
        JDialog frame = new JDialog();
        frame.setSize(
                description.getProperty(Integer.class,"width"),
                description.getProperty(Integer.class, "height")
        );
        frame.setTitle(description.getProperty(String.class,"title"));
        frame.setContentPane(content);
        frame.setLocationRelativeTo(null);

        int maxWidth = description.getProperty(Integer.class,"maxWidth");
        int maxHeight = description.getProperty(Integer.class,"maxHeight");

        Dimension max = frame.getMaximumSize();
        if (maxWidth > 0) {
            max.setSize(maxWidth,max.getHeight());
        }

        if (maxHeight > 0) {
            max.setSize(max.getWidth(),maxHeight);
        }
        frame.setMaximumSize(max);

        int minWidth = description.getProperty(Integer.class,"minWidth");
        int minHeight = description.getProperty(Integer.class,"minHeight");
        boolean resize = description.getProperty(Boolean.class, "resizeable");

        Dimension min = frame.getMinimumSize();
        if (minHeight > 0) {
            min.setSize(min.getWidth(),minHeight);
        }
        if (minWidth > 0) {
            min.setSize(minWidth, min.getHeight());
        }
        frame.setMinimumSize(min);
        frame.setResizable(resize);
        return frame;
    }

}
