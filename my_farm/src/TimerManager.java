import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

public class TimerManager {
    private long remainingTime;
    private Timer timer;
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
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                remainingTime--;
                if (remainingTime <= 0) {
                    timer.cancel();
                    isRunning = false;
                    Platform.runLater(onFinish);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 1000, 1000);
    }

    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
        isRunning = false;
    }
}
