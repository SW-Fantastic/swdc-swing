package org.swdc.xswing.view;

import jakarta.inject.Scope;
import org.swdc.dependency.annotations.ScopeImplement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Scope
@ScopeImplement(ViewManager.class)
public @interface XSwingView {

    String title();

    boolean dialog() default false;

    boolean resizeable() default true;

    boolean multiple() default false;

    int width() default 800;

    int height() default 600;

    int maxWidth() default -1;

    int maxHeight() default -1;

    int minWidth() default -1;

    int minHeight() default -1;

    Class controller() default ViewController.class;


}
