package application.music.playingView;

import application.utils.ImageUtil;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @Description 音乐播放界面
 * @author LIu Mingyao
 * @date 2019年4月6日下午10:17:55
 */
public class PlayingPanel {

	private PlayingPanel() {};

	private static Stage playingStage;
	private Stage mainStage;
	public static PlayingPanel obj;
	
	private double xOffset = 0; 
	private double yOffset = 0;

	static {
		obj = new PlayingPanel();
	}

	/**
	 * @Description 打开专用的音乐播放面板
	 * @param mainStage
	 * @author LIu Mingyao
	 */
	public void openPlayingState(Stage mainStage) {
		this.mainStage = mainStage;

		playingStage = new Stage();
		playingStage.initStyle(StageStyle.TRANSPARENT);// 窗口透明风格
		Button button = new Button("播放");
		AnchorPane pane = new AnchorPane();
		Scene scene = new Scene(pane, Color.AQUA);
		// scene透明
		scene.setFill(Paint.valueOf("#ffffff00"));

		Image image = new Image("/backgroundImg/preview.jpg");
		// 改变背景图片透明度
		WritableImage writableImage = new ImageUtil().imgOpacity(image, 0.4);
		// 将初始化的背景图片设置进播放界面
		ImageView iv = new ImageView(writableImage);
		
		//保持宽高比和高质量图片
		iv.setPreserveRatio(true);
		iv.setSmooth(true);
	
		
		pane.getChildren().add(iv);
		pane.getChildren().add(button);
		playingStage.setScene(scene);
		playingStage.show();
		double width = playingStage.getWidth();
		double height = playingStage.getHeight();

		
		//给图片设置圆角
		Rectangle rc=new Rectangle(width, height);
		rc.setArcHeight(50);
		rc.setArcWidth(50);
		iv.setClip(rc);
		
		
		System.out.println(width + " * " + height);
		button.setLayoutX(width / 2 - button.getWidth() / 2);
		button.setLayoutY(height - button.getHeight());

		Text textField = new Text("正在播放xxx");
		textField.setLayoutX(20);
		textField.setLayoutY(height / 2);
		textField.setFont(Font.font(24));
		textField.setFill(Color.RED);

		Text text1 = new Text("演唱者xxx");
		text1.setLayoutX(20);
		text1.setLayoutY(height / 2 + 40);
		// text1.setStyle("-fx-background-color:#B5B5B511");
		text1.setFont(Font.font(24));
		text1.setFill(Color.AQUA);

		pane.getChildren().add(textField);
		pane.getChildren().add(text1);
		// pane透明
		pane.setStyle("-fx-background-color:#B5B5B511");
		playingStage.show();

		mainStage.setIconified(true);
		
		
		//下面两个监听===》拖拽窗口
		pane.setOnMousePressed(event -> {
		    xOffset = event.getSceneX();
		    yOffset = event.getSceneY();
		});
		pane.setOnMouseDragged(event -> {
		    playingStage.setX(event.getScreenX() - xOffset);
		    playingStage.setY(event.getScreenY() - yOffset);
		});

	}

}
