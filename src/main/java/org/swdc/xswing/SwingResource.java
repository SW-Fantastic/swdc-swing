package org.swdc.xswing;

import java.io.File;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class SwingResource {

    private File assetFolder;

    private Class splash;

    private List<Class> configures;

    private List<String> args;

    private ThreadPoolExecutor executor;

    private Class<? extends ApplicationConfig> defaultConfig;

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setDefaultConfig(Class<? extends ApplicationConfig> defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    public Class<? extends ApplicationConfig> getDefaultConfig() {
        return defaultConfig;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public Class getSplash() {
        return splash;
    }

    public void setSplash(Class splash) {
        this.splash = splash;
    }

    public List<Class> getConfigures() {
        return configures;
    }

    public void setConfigures(List<Class> configures) {
        this.configures = configures;
    }

    public void setAssetFolder(File assetFolder) {
        this.assetFolder = assetFolder;
    }

    public File getAssetFolder() {
        return assetFolder;
    }
}
