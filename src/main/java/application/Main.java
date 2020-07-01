package application;

import application.controller.jfx.JfxController;
import application.config.Configuration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author MrLawrenc
 * date  2020/6/30 23:21
 * <p>
 * jfxapp
 * 启动详见{@link Launcher#main(String[])}方法
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {

            // Parent root = FXMLLoader.load(getClass().getResource("/application/Mars.fxml"));这种静态读取方法，无法获取到Controller的实例对象，就无法给controller注入值
            FXMLLoader fxmlLoader = new FXMLLoader(
                    getClass().getResource("/Mars.fxml"));
            Parent root = fxmlLoader.load();
            JfxController controller = fxmlLoader.getController();
            Scene scene = new Scene(root);
            controller.setScene(scene);
            controller.setMainStage(primaryStage);

            // 初始化綁定快捷鍵
            controller.shortcutKeys();

            primaryStage.setScene(scene);
            primaryStage.setTitle("mars的小工具 v" + Configuration.toolsVersion);

            //监听窗口关闭
            primaryStage.setOnCloseRequest(e -> System.exit(0));
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        // //播放界面图片初始化(透明度设置)
        // new Thread(() -> {
        // Image image = new Image("/1.jpg", 0, 0, true, true);
        // new ImageUtil().imgOpacity(image, 0.5);
        // },"播放界面图片初始化线程").start();

        // 配置文件初始化-装配所有属性
        Configuration.initProp();

        launch(args);

    }
}
