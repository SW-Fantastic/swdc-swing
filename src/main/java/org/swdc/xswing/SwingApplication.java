package org.swdc.xswing;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.IntelliJTheme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swdc.config.AbstractConfig;
import org.swdc.dependency.AnnotationLoader;
import org.swdc.dependency.DependencyContext;
import org.swdc.dependency.EnvironmentLoader;
import org.swdc.dependency.LoggerProvider;
import org.swdc.dependency.application.SWApplication;
import org.swdc.ours.common.StreamResources;
import org.swdc.ours.common.annotations.AnnotationDescription;
import org.swdc.ours.common.annotations.AnnotationDescriptions;
import org.swdc.ours.common.annotations.Annotations;
import org.swdc.xswing.font.Fontawsome5Service;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class SwingApplication implements SWApplication {

    private static final Logger logger = LoggerFactory.getLogger(SwingApplication.class);

    private SwingResource resources;

    private ThreadPoolExecutor asyncPool;

    private DependencyContext context;

    public void launch(String[] args) throws IOException {

        InputStream bannerInput = this.getClass().getModule().getResourceAsStream("banner/banner.txt");
        if (bannerInput == null) {
            bannerInput = SwingApplication.class.getModule().getResourceAsStream("banner/banner.txt");
        }
        String banner = StreamResources.readStreamAsString(bannerInput);
        System.out.println(banner);
        bannerInput.close();
        logger.info(" Application initializing..");

        AnnotationDescriptions annotations = Annotations.getAnnotations(this.getClass());
        AnnotationDescription appDesc = Annotations.findAnnotationIn(annotations,XSwingApplication.class);
        logger.info(" using assets: " + appDesc.getProperty(String.class,"assetsFolder"));
        Class[] configs = appDesc.getProperty(Class[].class,"configs");
        Class splash = appDesc.getProperty(Class.class,"splash");

        File file = null;
        String osName = System.getProperty("os.name").trim().toLowerCase();
        logger.info(" starting at : " + osName);
        if (osName.contains("mac")) {
            String url = this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
            String base = URLDecoder.decode(url, StandardCharsets.UTF_8);
            if (base.indexOf(".app") > 0) {
                // 位于MacOS的Bundle（.app软件包）内部，特殊处理以获取正确的路径。
                String location = base.substring(0,base.indexOf(".app")) + ".app/Contents/";
                Path target = new File(location).toPath();
                target = target.resolve(appDesc.getProperty(String.class,"assetsFolder"));
                file = target.toFile();
            } else {
                file = new File(appDesc.getProperty(String.class,"assetsFolder"));
            }
        } else {
            file = new File(appDesc.getProperty(String.class,"assetsFolder"));
        }

        logger.info(" dependency environment loading...");
        Optional<Class> config = Stream.of(configs)
                .filter(ApplicationConfig.class::isAssignableFrom).findAny();

        if (config.isEmpty()) {
            RuntimeException ex = new RuntimeException("请在XSwingApplication注解的configs里面添加一个继承自" +
                    "ApplicationConfig的配置类，来为应用提供Theme");
            logger.error(" 启动失败",ex);
            System.exit(0);
        }

        asyncPool = new ThreadPoolExecutor(4,12,30, TimeUnit.MINUTES,new LinkedBlockingQueue<>());

        resources = new SwingResource();
        resources.setArgs(Arrays.asList(args));
        resources.setDefaultConfig(config.get());
        resources.setAssetFolder(file);
        resources.setConfigures(Arrays.asList(configs));
        resources.setSplash(splash);
        resources.setExecutor(asyncPool);

        try {

            Splash view = (Splash) resources.getSplash()
                    .getConstructor( SwingResource.class)
                    .newInstance(resources);
            view.show();

            AnnotationLoader loader = new AnnotationLoader();
            CompletableFuture
                    .supplyAsync(() -> this.loadConfigs(loader),asyncPool)
                    .thenApplyAsync(ctx -> this.context = ctx.load(),asyncPool)
                    .thenRunAsync(() -> {
                        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                            this.onShutdown(context);
                            this.asyncPool.shutdown();
                            logger.info(" environment has shutdown.");
                        }));

                        ApplicationConfig mainConfig = this.context.getByClass(resources.getDefaultConfig());
                        try {

                            System.setProperty("flatlaf.useWindowDecorations","true" );
                            System.setProperty("flatlaf.menuBarEmbedded","true");

                            if (mainConfig.getTheme().isBlank()) {
                                FlatLightLaf laf = new FlatLightLaf();
                                UIManager.setLookAndFeel(laf);
                            } else {
                                FileInputStream fin = new FileInputStream(resources.getAssetFolder().getAbsolutePath() + File.separator + mainConfig.getTheme() + ".theme.json");
                                IntelliJTheme.ThemeLaf laf = new IntelliJTheme.ThemeLaf(new IntelliJTheme(fin));
                                UIManager.setLookAndFeel(laf);
                            }

                            this.onStarted(context);
                            view.hide();

                        } catch (Exception e) {
                            logger.error("failed to setup application ", e);
                        }

                    });

        } catch (Exception e) {
            logger.error(" - failed to start application ", e);
        }


    }

    private AnnotationLoader loadConfigs(AnnotationLoader loader) {
        this.onConfig(loader);
        List<Class> configures = resources.getConfigures();
        for (Class conf: configures) {
            try {
                AbstractConfig confInstance = (AbstractConfig) conf.getConstructor().newInstance();
                loader.withInstance(conf,confInstance);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        logger.info(" config loaded.");
        loader.withProvider(LoggerProvider.class);
        loader.withInstance(SwingResource.class,resources);
        loader.withInstance(ThreadPoolExecutor.class,asyncPool);
        loader.withInstance(SwingApplication.class,this);
        loader.withInstance(Fontawsome5Service.class,new Fontawsome5Service());
        return loader;
    }

    @Override
    public void onConfig(EnvironmentLoader environmentLoader) {

    }

    @Override
    public void onStarted(DependencyContext dependencyContext) {

    }

    @Override
    public void onShutdown(DependencyContext dependencyContext) {

    }

}
