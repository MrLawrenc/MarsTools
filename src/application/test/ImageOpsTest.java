package application.test;

import application.utils.ImageUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.image.WritableImage;

public class ImageOpsTest extends Application {

	@Override
	public void start(Stage primaryStage) {

		// 创建Image和ImageView对象
		Image image = new Image("/2.PNG");
		ImageView imageView = new ImageView();

		//改变图片透明度
		WritableImage wImage = new ImageUtil().imgOpacity(image, 0.05);

		// 在屏幕上显示图像
		imageView.setImage(wImage);
		StackPane root = new StackPane();
		root.getChildren().add(imageView);
		Scene scene = new Scene(root);
		
		//stage和scene透明设置
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		scene.setFill(Paint.valueOf("#ffffff00"));
		
		
		primaryStage.setTitle("Image Write Test");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}