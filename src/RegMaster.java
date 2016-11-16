import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.http.client.utils.URIBuilder;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

import static java.util.Calendar.DATE;
import static javafx.geometry.Pos.*;

public class RegMaster extends Application {

    private static Label[] slnLabels = new Label[10];
    private static TextField[] slnTextFields = new TextField[10];
    private static TextField cwTextField;
    private static TextField yearTextField;
    private static ChoiceBox<String> quarterChoiceBox;
    private static ChoiceBox<String> regTimeChoiceBox;
    private static Label regTimeLabel;
    private static Timer timer;

    private static final ObservableList<String> REG_TIME_LIST = FXCollections
            .observableArrayList("6:00", "0:00");

    private static final ObservableList<String> QTR_LIST = FXCollections
            .observableArrayList("Winter", "Spring", "Summer", "Autumn");

    public static void main(String[] args) {
        launch(args);
    }

    private static void timedReg() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        Date regTime = getRegTime().getTime();
        timer.schedule(new RegisterTask(getUrl()), regTime);
        regTimeLabel.setText("Sched: " + regTime.toString());
    }

    private static URL getUrl() {
        try {
            URIBuilder builder = new URIBuilder("https://sdb.admin.uw" +
                    ".edu/students/uwnetid/register.asp");
            builder.addParameter("QTR", Integer.toString(getQuarterNumber()));
            builder.addParameter("YR", yearTextField.getText());
            builder.addParameter("INPUTFORM", "UPDATE");
            builder.addParameter("PAC", "0");
            builder.addParameter("MAXDROPS", "0");
            builder.addParameter("_CW", cwTextField.getText());
            for (int i = 0; i < 10; i++) {
                builder.addParameter
                        ("sln" + (i + 1), slnTextFields[i].getText().trim());
                builder.addParameter
                        ("entcode" + (i + 1), "");
                builder.addParameter
                        ("credits" + (i + 1), "");
                builder.addParameter
                        ("gr_sys" + (i + 1), "");
            }
            return builder.build().toURL();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getQuarterNumber() {
        switch (quarterChoiceBox.getValue()) {
            case "Winter":
                return 1;
            case "Spring":
                return 2;
            case "Summer":
                return 3;
            case "Autumn":
                return 4;
            default:
                return 0;
        }
    }

    private static Calendar getRegTime() {
        Calendar regTime = Calendar.getInstance();
        String[] timeSelected = regTimeChoiceBox.getValue().split(":");
        int hour = Integer.parseInt(timeSelected[0]);
        int minute = Integer.parseInt(timeSelected[1]);
        regTime.set(Calendar.HOUR_OF_DAY, hour);
        regTime.set(Calendar.MINUTE, minute);
        regTime.set(Calendar.SECOND, 0);
        if (Calendar.getInstance().compareTo(regTime) > 0) {
            regTime.add(DATE, 1);
        }
        return regTime;
    }

    private static void initGui(Stage primaryStage) {
        primaryStage.setTitle("UW Registration Master");
        GridPane grid = new GridPane();
        grid.setAlignment(CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        Text sceneTitleText = new Text("Registration");
        sceneTitleText.setFont(Font.font(null, FontWeight.NORMAL, 20));
        grid.add(sceneTitleText, 0, 0, 2, 1);

        quarterChoiceBox = new ChoiceBox<>(QTR_LIST);
        quarterChoiceBox.getSelectionModel().selectFirst();
        grid.add(quarterChoiceBox, 0, 1);
        yearTextField = new TextField("2017");
        grid.add(yearTextField, 1, 1, 2, 1);

        Label cwLabel = new Label("CW");
        grid.add(cwLabel, 0, 2);
        cwTextField = new TextField();
        cwTextField.focusedProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (!newValue) {
                        cwTextField.setText(extractCwCode(cwTextField
                                .getText()));
                    }
                });
        cwTextField.setPromptText("Copy url from MyPlan");
        grid.add(cwTextField, 1, 2, 2, 1);

        Label clock = new DigitalClock();
        grid.add(clock, 0, 3, 2, 1);

        for (int i = 0; i < 10; i++) {
            slnLabels[i] = new Label("SLN " + (i + 1));
            grid.add(slnLabels[i], 0, i + 4);
            slnTextFields[i] = new TextField();
            slnTextFields[i].setPromptText("5 digit SLN");
            grid.add(slnTextFields[i], 1, i + 4, 2, 1);
        }

        Button autoButton = new Button("Auto");
        autoButton.setStyle("-fx-base: #ffcc33;");
        autoButton.setOnAction(event -> timedReg());
        HBox hbAutoButton = new HBox(10);
        hbAutoButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbAutoButton.getChildren().add(autoButton);
        grid.add(hbAutoButton, 1, 14);

        regTimeChoiceBox = new ChoiceBox<>(REG_TIME_LIST);
        regTimeChoiceBox.getSelectionModel().selectFirst();
        grid.add(regTimeChoiceBox, 2, 14);

        Button goNowButton = new Button("Go now");
        HBox hbGoNowButton = new HBox(10);
        hbGoNowButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbGoNowButton.getChildren().add(goNowButton);
        goNowButton.setOnAction(event -> RegisterTask.openWebpage(getUrl()));
        goNowButton.setStyle("-fx-base: #9900cc;");
        grid.add(hbGoNowButton, 0, 14);

        regTimeLabel = new Label();
        grid.add(regTimeLabel, 0, 15, 3, 1);

        Scene scene = new Scene(grid, 270, 550);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static String extractCwCode(String raw) {
        String result;
        if (raw.startsWith("http")) {
            result = raw.split("&")[3].substring(4);
        } else {
            result = raw;
        }
        System.out.println(result);
        return result.trim();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initGui(primaryStage);
    }

}
