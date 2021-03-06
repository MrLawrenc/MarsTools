package application.music.online.kuwo.view;

import application.utils.ImageUtil;
import application.utils.LyricShowUtil;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 * 
 *  音乐播放界面
 * @date 2019年4月6日下午10:17:55
 */
public class PlayingPanel {

    public static PlayingPanel obj;
    public Stage playingStage;
    private AnchorPane pane;
    private double width;
    private double height;
    private Button playButton;

    public MediaPlayer player;
    private final boolean repeat = false;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration;
    private Slider timeSlider;
    private Label playTime;
    private Slider volumeSlider;

    private double xOffset = 0;
    private double yOffset = 0;

    static {
        obj = new PlayingPanel();
    }

    /**
     * 打开专用的音乐播放面板
     */
    public void openPlayingState(Stage mainStage, MediaPlayer mainPlayer,
                                 LyricShowUtil lyricShowUtil) {

        if (player != mainPlayer || player == null) {
            this.player = mainPlayer;
            duration = player.getMedia().getDuration();
            // 不是之前的player对象菜添加player各种监听
            playerListener();
        }

        player.play();

        if (obj.playingStage == null) {
            playingStage = new Stage();
            playingStage.initStyle(StageStyle.TRANSPARENT);// 窗口透明风格
            pane = new AnchorPane();
            Scene scene = new Scene(pane, Color.AQUA);
            // scene透明
            scene.setFill(Paint.valueOf("#ffffff00"));

            // 设置背景图片并改变背景图片透明度
            WritableImage writableImage = new ImageUtil().setBackgroundImg(0.4);
            // 将初始化的背景图片设置进播放界面
            ImageView iv = new ImageView(writableImage);

            // 保持宽高比和高质量图片
            iv.setPreserveRatio(true);
            iv.setSmooth(true);

            pane.getChildren().add(iv);
            playingStage.setScene(scene);
            playingStage.show();
            width = playingStage.getWidth();
            height = playingStage.getHeight();

            // 给图片设置圆角
            Rectangle rc = new Rectangle(width, height);
            rc.setArcHeight(50);
            rc.setArcWidth(50);
            iv.setClip(rc);
            // System.out.println(width + " * " + height);

            // 添加播放器各组件
            addLabelAssembly(lyricShowUtil, mainStage);

            // pane透明
            pane.setStyle("-fx-background-color:#B5B5B500");
            playingStage.show();

            // 下面两个监听===》拖拽窗口
            pane.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            pane.setOnMouseDragged(event -> {
                playingStage.setX(event.getScreenX() - xOffset);
                playingStage.setY(event.getScreenY() - yOffset);
            });

            // 窗口关闭监听事件
            playingStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    lyricShowUtil.lyricThread.interrupt();

                    player.stop();
                    mainStage.setIconified(false);
                }

            });
        } else {
            obj.playingStage.show();
        }

        mainStage.setIconified(true);
    }

    /**
     *  player的各种监听事件
     * 
     */
    private void playerListener() {
        player.setOnPlaying(new Runnable() {
            public void run() {
                if (stopRequested) {
                    player.pause();
                    stopRequested = false;
                } else {
                    ImageView imageView = changeBtnPlayImg(playButton);
                    playButton.setGraphic(imageView);
                }
            }
        });

        player.setOnPaused(new Runnable() {
            public void run() {
                System.out.println("onPaused");
                ImageView imageView = changeBtnStopImg(playButton);
                playButton.setGraphic(imageView);
            }
        });

        player.setOnReady(new Runnable() {
            public void run() {
                // System.out.println(player.getMedia().getDuration() + "00");
                // duration = player.getMedia().getDuration();
                // System.out.println(duration);
                updateValues();
            }
        });

        player.setCycleCount(repeat ? MediaPlayer.INDEFINITE : 1);
        player.setOnEndOfMedia(new Runnable() {
            public void run() {
                if (!repeat) {
                    playButton.setText(">");
                    stopRequested = true;
                    atEndOfMedia = true;
                }
            }
        });

        // 时间改变监听(0.1秒)
        player.currentTimeProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                updateValues();
            }
        });

    }

    /**
     *  添加播放面板各组件
     * 
     */
    private void addLabelAssembly(LyricShowUtil lyricShowUtil, Stage mainStage) {
        // 关闭按钮
        Button close = new Button();
        // 透明
        close.setStyle("-fx-background-color:#B5B5B500");
        ImageView closeImg = initCloseBtnyImg(close);
        close.setGraphic(closeImg);
        close.setLayoutX(width - 35);
        close.setLayoutY(10);
        close.setOnAction((event) -> {
            lyricShowUtil.lyricThread.interrupt();
            player.stop();
            playingStage.close();
            mainStage.setIconified(false);
        });
        pane.getChildren().add(close);

        // 最小化按钮
        Button small = new Button();
        small.setStyle("-fx-background-color:#B5B5B500");
        ImageView smallImg = initSmallBtnyImg(small);
        small.setGraphic(smallImg);
        small.setLayoutX(width - 65);
        small.setLayoutY(10);
        small.setOnAction((event) -> {
            playingStage.setIconified(true);// 最小化
        });
        pane.getChildren().add(small);

        // 添加播放按钮
        playButton = new Button();
        playButton.setStyle("-fx-background-color:#B5B5B500");
        ImageView imageView = changeBtnPlayImg(playButton);
        playButton.setGraphic(imageView);
        playButton.setLayoutX(35);
        playButton.setLayoutY(height - 50);
        // 播放按钮监听
        playButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                Status status = player.getStatus();
                if (status == Status.UNKNOWN || status == Status.HALTED) {
                    // don't do anything in these states
                    return;
                }
                if (status == Status.PAUSED || status == Status.READY || status == Status.STOPPED) {
                    // rewind the movie if we're sitting at the end
                    if (atEndOfMedia) {
                        player.seek(player.getStartTime());
                        atEndOfMedia = false;
                    }
                    player.play();
                } else {
                    player.pause();
                }
            }
        });
        pane.getChildren().add(playButton);

        // Add Time label
        Text timeText = new Text("Time: ");
        timeText.setFill(Paint.valueOf("#FF34B3"));
        timeText.setLayoutX(playButton.getLayoutX() + 50);
        timeText.setLayoutY(playButton.getLayoutY() + 18);
        pane.getChildren().add(timeText);

        // Add time slider
        timeSlider = new Slider();
        timeSlider.setLayoutX(playButton.getLayoutX() + 80);
        timeSlider.setLayoutY(height - 42);
        timeSlider.setMinWidth(450);
        timeSlider.setMaxWidth(Double.MAX_VALUE);
        timeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (timeSlider.isValueChanging()) {
                    // multiply duration by percentage calculated by slider position
                    player.seek(duration.multiply(timeSlider.getValue() / 100.0));
                }
            }
        });
        pane.getChildren().add(timeSlider);

        playTime = new Label();
        // playTime.setStyle("-fx-background-color:#FF34B3");
        playTime.setLayoutX(playButton.getLayoutX() + 560);
        playTime.setLayoutY(height - 45);
        playTime.setPrefWidth(130);
        playTime.setMinWidth(50);
        pane.getChildren().add(playTime);

        // Add the volume label
        Button volumeBtn = new Button();
        volumeBtn.setLayoutX(width - 120 - 40);
        volumeBtn.setLayoutY(playButton.getLayoutY());
        pane.getChildren().add(initVolImg(volumeBtn));

        // Add Volume slider
        volumeSlider = new Slider();
        volumeSlider.setLayoutX(width - 100 - 20);
        volumeSlider.setLayoutY(height - 45);
        volumeSlider.setPrefWidth(100);
        volumeSlider.setMaxWidth(Region.USE_PREF_SIZE);
        volumeSlider.setMinWidth(30);

        volumeSlider.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov) {
                if (volumeSlider.isValueChanging()) {
                    player.setVolume(volumeSlider.getValue() / 100.0);
                }
            }
        });
        pane.getChildren().add(volumeSlider);

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
    }

    protected void updateValues() {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(new Runnable() {
                @SuppressWarnings("deprecation")
                public void run() {
                    Duration currentTime = player.getCurrentTime();
                    playTime.setText(formatTime(currentTime, duration));
                    timeSlider.setDisable(duration.isUnknown());
                    if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO)
                            && !timeSlider.isValueChanging()) {
                        timeSlider.setValue(currentTime.divide(duration).toMillis() * 100.0);
                    }
                    if (!volumeSlider.isValueChanging()) {
                        volumeSlider.setValue((int) Math.round(player.getVolume() * 100));
                    }
                }
            });
        }
    }

    private static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;
        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes,
                        elapsedSeconds, durationHours, durationMinutes, durationSeconds);
            } else {
                return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds,
                        durationMinutes, durationSeconds);
            }
        } else {
            if (elapsedHours > 0) {
                return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
            } else {
                return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);
            }
        }
    }

    public ImageView changeBtnPlayImg(Button button) {
        Image playImg = new Image(getClass().getResourceAsStream("/img/play2.png"));
        ImageView imageView = new ImageView(playImg);
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);
        return imageView;
    }

    public ImageView initCloseBtnyImg(Button button) {
        Image closeImage = new Image(getClass().getResourceAsStream("/img/close1.png"));
        ImageView imageView = new ImageView(closeImage);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        return imageView;
    }

    public ImageView initSmallBtnyImg(Button button) {
        Image smallImage = new Image(getClass().getResourceAsStream("/img/small.png"));
        ImageView imageView = new ImageView(smallImage);
        imageView.setFitWidth(16);
        imageView.setFitHeight(16);
        return imageView;
    }

    public ImageView changeBtnStopImg(Button button) {
        Image playImg = new Image(getClass().getResourceAsStream("/img/stop.png"));
        ImageView imageView = new ImageView(playImg);
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);
        return imageView;
    }

    public Button initVolImg(Button volButton) {
        volButton.setStyle("-fx-background-color:#B5B5B500");
        Image playImg = new Image(getClass().getResourceAsStream("/img/vol.png"));
        ImageView imageView = new ImageView(playImg);
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);
        volButton.setGraphic(imageView);
        return volButton;
    }

}
