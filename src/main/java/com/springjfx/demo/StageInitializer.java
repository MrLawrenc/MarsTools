package com.springjfx.demo;

import application.controller.jfx.JfxController;
import application.config.Configuration;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * @author : LiuMingyao
 * @date : 2020/4/4 10:33
 * @description : TODO
 */
@Component
public class StageInitializer implements ApplicationListener<StageInitializer.StageReadyEvent> {
    @Value("classpath:/chart.fxml")
    private Resource chartResource;

    @Value("${stageTitle}")
    private String stageTitle;
    private ApplicationContext applicationContext;

    public StageInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent stageReadyEvent) {
        Stage stage = stageReadyEvent.getStage();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(chartResource.getURL());
            fxmlLoader.setControllerFactory(aClass -> applicationContext.getBean(aClass));
            Parent parent = fxmlLoader.load();


            stage.setScene(new Scene(parent, 800, 600));
            stage.setTitle(stageTitle);
            stage.show();

           // showToolStage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showToolStage(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/Mars.fxml"));
        Parent root = fxmlLoader.load();
        JfxController controller = fxmlLoader.getController();
        Scene scene = new Scene(root);
        controller.setScene(scene);
        controller.setMainStage(stage);

        // 初始化綁定的快捷鍵
        controller.shortcutKeys();

        stage.setScene(scene);
        stage.setTitle("mars的小工具 v" + Configuration.toolsVersion);

        //监听窗口关闭
        //stage.setOnCloseRequest(e ->System.exit(0));
        stage.show();
    }

    public static class StageReadyEvent extends ApplicationEvent {
        public StageReadyEvent(Stage stage) {
            super(stage);
        }

        public Stage getStage() {
            return (Stage) getSource();
        }
    }
}