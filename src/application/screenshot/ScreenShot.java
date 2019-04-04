package application.screenshot;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * @Description 截图工具类
 * @author LIu Mingyao
 * @date 2019年4月1日下午6:03:54
 */
public class ScreenShot {

	private ScreenShot() {};

	private static Stage screenStage;
	private Stage mainStage;
	private HBox hBox;

	private double start_x;
	private double start_y;
	private double end_x;
	private double end_y;

	/**
	 * 初始化ScreenShot各组件
	 * 
	 * @Description TODO
	 * @return
	 * @author LIu Mingyao
	 */
	public static ScreenShot initScreenShot() {
		screenStage = new Stage();
		return new ScreenShot();
	}

	public void showScreenPanel(Stage stage) {
		mainStage = stage;

		// 先最小化当前面板,再创建截图面板
		stage.setIconified(true);

		AnchorPane pane = new AnchorPane();
		pane.setStyle("-fx-background-color:#B5B5B511");
		Scene scene = new Scene(pane);
		scene.setFill(Paint.valueOf("#ffffff00"));

		screenStage.setFullScreenExitHint("esc exit");
		screenStage.setScene(scene);
		screenStage.setFullScreen(true);
		screenStage.initStyle(StageStyle.TRANSPARENT);
		screenStage.show();

		startScreen(scene, pane);
	}

	public void startScreen(Scene scene, AnchorPane pane) {
		// 截图面板按键监听
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {

				// 按esc就退出截圖界面，并且還原主界面
				if (event.getCode() == KeyCode.ESCAPE) {
					screenStage.close();
					mainStage.setIconified(false);
				}

			}
		});

		// 鼠标按压监听，画出截图的区域
		pane.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {

				pane.getChildren().clear();

				hBox = new HBox();
				hBox.setBackground(null);
				hBox.setBorder(new Border(new BorderStroke(Paint.valueOf("#CD3700"),
						BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

				start_x = event.getSceneX();
				start_y = event.getSceneY();

				AnchorPane.setLeftAnchor(hBox, start_x);
				AnchorPane.setTopAnchor(hBox, start_y);

				pane.getChildren().add(hBox);
			}
		});

		// 鼠标拖拽监听
		pane.setOnDragDetected(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				pane.startFullDrag();

			}
		});

		// 拖拽过程监听
		pane.setOnMouseDragOver(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				end_x = event.getSceneX();
				end_y = event.getSceneY();

				Label label = new Label();
				label.setAlignment(Pos.CENTER);
				label.setPrefHeight(30);
				label.setPrefWidth(160);
				label.setTextFill(Paint.valueOf("#FAFAD2"));
				label.setStyle("-fx-background-color:#B5B5B5");

				AnchorPane.setLeftAnchor(label, start_x);
				// 根据屏幕高度(此时是全屏的)动态设定显示截取到图片的大小
				if (start_y - label.getPrefHeight() > 0) {
					AnchorPane.setTopAnchor(label, start_y - label.getPrefHeight());
				} else {
					AnchorPane.setTopAnchor(label, start_y);
				}

				pane.getChildren().add(label);

				double width = end_x - start_x;
				double height = end_y - start_y;

				hBox.setPrefHeight(height);
				hBox.setPrefWidth(width);

				label.setText("宽：" + width + " 高：" + height);
			}
		});

		// 鼠标拖拽结束显示保存截图按钮
		pane.setOnMouseDragExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				Button saveImg = new Button();
				saveImg.setText("保存截图");

				hBox.getChildren().add(saveImg);
				hBox.setAlignment(Pos.BOTTOM_RIGHT);
				saveImg.setOnAction(new EventHandler<ActionEvent>() {

					@Override
					public void handle(ActionEvent event) {
						// 获取并保存截图
						saveScreenImg();
					}
				});
			}
		});

	}

	/**
	 * @Description 获取到截图的图片并保存
	 * @author LIu Mingyao
	 */
	public void saveScreenImg() {
		screenStage.close();

		try {
			// 截图
			Robot robot = new Robot();
			Rectangle rc = new Rectangle((int) start_x, (int) start_y, (int) (end_x - start_x),
					(int) (end_y - start_y));
			BufferedImage image = robot.createScreenCapture(rc);

			// 转换为fx的image，(后面可以展示在Image面板上,待做)
			WritableImage fxImage = SwingFXUtils.toFXImage(image, null);

			// 获取系统剪切板并且设置截图到剪切板上
			Clipboard cb = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();
			content.putImage(fxImage);
			cb.setContent(content);
			System.out.println("截图已保存至系统剪切板!");

			// 保存到本地文件
			SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-DD_HH-mm-ss");
			ImageIO.write(image, "png", new File(
					"C:/Users/LIu Mingyao/Pictures/" + format.format(new Date()) + ".png"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mainStage.setIconified(false);
	}

}
