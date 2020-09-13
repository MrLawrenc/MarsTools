package application.music.local;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * @author : MrLawrenc
 * date  2020/9/12 16:27
 */
public class LocalMusicMainView {
    //2020-09-13 20:26:40
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }

    /**
     * 防止gc回收咯，可以试着加载局部变量  放着放着会被回收
     */
    static MediaPlayer mediaPlayer;

    public void open() {


        Media media = new Media(LocalMusicMainView.class.getResource("/ccw.mp3").toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(mediaPlayer::play);

        ObservableMap<String, Object> metadata = media.getMetadata();

       mediaPlayer.setOnPlaying(()->{
           Duration startTime = mediaPlayer.getStartTime();
           Duration stopTime1 = mediaPlayer.getMedia().getDuration();
           Duration stopTime = mediaPlayer.getStopTime();
           System.out.println("start "+startTime+" to "+stopTime+" "+stopTime1);
       });


        mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("change " + oldValue + " to " + newValue);

        });
        mediaPlayer.setOnPaused(() -> {
            System.out.println("sssssssssss");
        });

        mediaPlayer.onStoppedProperty().addListener(new ChangeListener<Runnable>() {
            @Override
            public void changed(ObservableValue<? extends Runnable> observable, Runnable oldValue, Runnable newValue) {
                System.out.println(newValue);
            }
        });
        mediaPlayer.errorProperty().addListener(new ChangeListener<MediaException>() {
            @Override
            public void changed(ObservableValue<? extends MediaException> observable, MediaException oldValue, MediaException newValue) {
                System.out.println(newValue);
            }
        });
/*
        new Thread(() -> {
            while (true) {
                System.out.println(mediaPlayer.getStatus());
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/

    }
}