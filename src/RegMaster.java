import javafx.application.Application;
import javafx.collections.FXCollections;
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
import java.util.Timer;

import static javafx.geometry.Pos.*;

public class RegMaster extends Application {

    private static Label[] slnLabels = new Label[10];
    private static TextField[] slnTextFields = new TextField[10];
    private static TextField cwTextField;
    private static TextField yearTextField;
    private static ChoiceBox<String> quarterChoiceBox;
    private static ChoiceBox<String> regTimeChoiceBox;

    public static void main(String[] args) {
        launch(args);
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
                        ("sln" + (i + 1), slnTextFields[i].getText());
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
        int hour = Integer.parseInt(regTimeChoiceBox.getValue().split(":")[0]);
        regTime.set(Calendar.HOUR_OF_DAY, hour);
        regTime.set(Calendar.MINUTE, 0);
        regTime.set(Calendar.SECOND, 0);
        return regTime;
    }

    private static void initGui(Stage primaryStage) {
        primaryStage.setTitle("UW Registration Master");
        GridPane grid = new GridPane();
        grid.setAlignment(CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text sceneTitleText = new Text("Reg Master");
        sceneTitleText.setFont(Font.font(null, FontWeight.NORMAL, 20));
        grid.add(sceneTitleText, 0, 0, 2, 1);

        quarterChoiceBox = new ChoiceBox<>(FXCollections
                .observableArrayList(
                        "Winter", "Spring", "Summer", "Autumn")
        );
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
                        System.out.println("Textfield out focus");
                        cwTextField.setText(extractCwCode(cwTextField
                                .getText()));
                    }
                });
        grid.add(cwTextField, 1, 2, 2, 1);

        Label clock = new DigitalClock();
        grid.add(clock, 0, 3, 2, 1);

        for (int i = 0; i < 10; i++) {
            slnLabels[i] = new Label("SLN " + (i + 1));
            grid.add(slnLabels[i], 0, i + 4);
            slnTextFields[i] = new TextField();
            grid.add(slnTextFields[i], 1, i + 4, 2, 1);
        }

        Button autoButton = new Button("Auto");
        HBox hbAutoButton = new HBox(10);
        hbAutoButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbAutoButton.getChildren().add(autoButton);
        grid.add(hbAutoButton, 1, 14);
        autoButton.setOnAction(event -> {
            Timer timer = new Timer();
            timer.schedule(new RegisterTask(getUrl()), getRegTime().getTime());
        });

        regTimeChoiceBox = new ChoiceBox<>(FXCollections
                .observableArrayList("6:00", "0:00")
        );
        regTimeChoiceBox.getSelectionModel().selectFirst();
        grid.add(regTimeChoiceBox, 2, 14);

        Button goNowButton = new Button("Go now");
        HBox hbGoNowButton = new HBox(10);
        hbGoNowButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbGoNowButton.getChildren().add(goNowButton);
        goNowButton.setOnAction(event -> RegisterTask.openWebpage(getUrl()));
        grid.add(hbGoNowButton, 0, 14);

        Scene scene = new Scene(grid, 300, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static String extractCwCode(String raw) {
        if (raw.startsWith("http")) {
            String result = raw.split("&")[3].substring(4);
            System.out.println(result);
            return result;
        }
        return raw;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        initGui(primaryStage);
    }

}
