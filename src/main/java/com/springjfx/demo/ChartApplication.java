package com.springjfx.demo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author : LiuMingyao
 * @date : 2020/4/4 10:26
 * @description : TODO
 */
public class ChartApplication extends Application {
    private ConfigurableApplicationContext applicationContext;


    Logger logger = LoggerFactory.getLogger(ChartApplication.class);

    @Override
    public void stop() {
        logger.info("app will stop.............");
        applicationContext.close();
        Platform.exit();

    }

    @Override
    public void init() {
        applicationContext = new SpringApplicationBuilder(JfxbootApplication.class).run();
    }

    @Override
    public void start(Stage stage) {
        applicationContext.publishEvent(new StageInitializer.StageReadyEvent(stage));
    }
}