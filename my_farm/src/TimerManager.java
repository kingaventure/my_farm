import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.util.Duration;

public class TimerManager {
    private long remainingTime;
    private Timeline timeline;
    private boolean isRunning;

    public TimerManager(long remainingTime) {
        this.remainingTime = remainingTime;
        this.isRunning = false;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void start(Runnable onFinish) {
        if (isRunning) {
            return;
        }
        isRunning = true;
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingTime--;
            if (remainingTime <= 0) {
                stop();
                Platform.runLater(onFinish);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
        }
        isRunning = false;
    }
}