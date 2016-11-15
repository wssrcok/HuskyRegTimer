import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.Calendar;

class DigitalClock extends Label {
    public DigitalClock() {
        bindToTime();
    }

    // the digital clock updates once a second.
    private void bindToTime() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        actionEvent -> {
                            Calendar time = Calendar.getInstance();
                            String hourString = StringUtilities.pad(2, ' ', time.get(Calendar.HOUR) == 0 ? "12" : time.get(Calendar.HOUR) + "");
                            String minuteString = StringUtilities.pad(2, '0', time.get(Calendar.MINUTE) + "");
                            String secondString = StringUtilities.pad(2, '0', time.get(Calendar.SECOND) + "");
                            String ampmString = time.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
                            setText("Current time: " + hourString + ":" +
                                    minuteString + ":" + secondString + " " + ampmString);
                        }
                ),
                new KeyFrame(Duration.seconds(1))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }
}
