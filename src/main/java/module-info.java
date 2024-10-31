module swdc.application.swing {

    requires java.desktop;
    requires swdc.application.dependency;
    requires swdc.application.configs;
    requires swdc.commons;
    requires swdc.swing;
    requires jakarta.inject;
    requires com.formdev.flatlaf;

    requires org.slf4j;

    exports org.swdc.xswing.view;
    exports org.swdc.xswing.font;
    exports org.swdc.xswing;

    opens org.swdc.xswing to swdc.application.configs;

}