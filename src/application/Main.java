package application;

import java.util.List;

import application.controller.MyController;
import application.utils.ConfigurationFileUtil;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	static ConfigurationFileUtil myConf = ConfigurationFileUtil.obj;
	@Override
	public void start(Stage primaryStage) {
		try {

			// Parent root = FXMLLoader.load(getClass().getResource("/application/Mars.fxml"));这种静态读取方法，无法获取到Controller的实例对象，就无法各给controller注入值
			FXMLLoader fxmlLoader = new FXMLLoader(
					getClass().getResource("/application/Mars.fxml"));
			Parent root = fxmlLoader.load();
			MyController controller = fxmlLoader.getController();

			Scene scene = new Scene(root);
			controller.setScene(scene);
			controller.setMainStage(primaryStage);

			// 初始化綁定的快捷鍵
			controller.shortcutKeys();

			primaryStage.setScene(scene);
			primaryStage.setTitle("mars的小工具 v" + myConf.toolsVersion);

			//监听窗口关闭
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					System.exit(0);
				}
			});
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
		List<String> props = ConfigurationFileUtil.obj.getPropFromConfigFile();
		myConf.assemblyProps(props);
		launch(args);

	}
}
