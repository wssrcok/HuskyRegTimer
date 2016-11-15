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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import static javafx.geometry.Pos.*;

public class RegMaster extends Application {

    public final static String REG_TIME = "2016-11-15 13:26:00";
    public final static int QTR_NUM = 1;
    public final static int YEAR = 2017;
    public final static String CW =
            "ed58595ff0eb7e42a37bd7a7fb8b8467de55569961d60afdf93a2c5d43dd05cd";


    private static Label[] slnLabels = new Label[10];
    private static TextField[] slnTextFields = new TextField[10];
    private static TextField cwTextField;
    private static TextField yearTextField;
    private static ChoiceBox<String> quarterChoiceBox;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("UW Registration Master");
        GridPane grid = new GridPane();
        grid.setAlignment(CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Reg Master");
        scenetitle.setFont(Font.font(null, FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        quarterChoiceBox = new ChoiceBox<>(FXCollections
                .observableArrayList(
                "Wi", "Sp", "Su", "Au")
        );
        grid.add(quarterChoiceBox, 0, 1);
        yearTextField = new TextField("2017");
        grid.add(yearTextField, 1, 1);

        Label cwLabel = new Label("CW");
        grid.add(cwLabel, 0, 2);
        cwTextField = new TextField();
        grid.add(cwTextField, 1, 2);

        Label clock = new DigitalClock();
        grid.add(clock, 0, 3, 2, 1);

        for (int i = 0; i < 10; i++) {
            slnLabels[i] = new Label("SLN " + (i+1));
            grid.add(slnLabels[i], 0, i+4);
            slnTextFields[i] = new TextField();
            grid.add(slnTextFields[i], 1, i+4);
        }

        Button autoButton = new Button("Auto");
        HBox hbAutoButton = new HBox(10);
        hbAutoButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbAutoButton.getChildren().add(autoButton);
        grid.add(hbAutoButton, 0, 14);
        autoButton.setOnAction(event -> {
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = dateFormatter.parse(REG_TIME);
                Timer timer = new Timer();
                timer.schedule(new RegisterTask(getUrl()), date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

        Button goNowButton = new Button("Go now");
        HBox hbGoNowButton = new HBox(10);
        hbGoNowButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbGoNowButton.getChildren().add(goNowButton);
        goNowButton.setOnAction(event -> {
            RegisterTask.openWebpage(getUrl());
        });
        grid.add(hbGoNowButton, 1, 14);

        Scene scene = new Scene(grid, 300, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private static URL getUrl() {
        try {
            URIBuilder builder = new URIBuilder("https://sdb.admin.uw" +
                    ".edu/students/uwnetid/register.asp");
            builder.addParameter("QTR", quarterChoiceBox.getValue());
            builder.addParameter("YR", yearTextField.getText());
            builder.addParameter("INPUTFORM", "UPDATE");
            builder.addParameter("PAC", "0");
            builder.addParameter("MAXDROPS", "0");
            builder.addParameter("_CW", cwTextField.getText());
            for (int i = 0; i < 10; i++) {
                builder.addParameter
                        ("sln" + (i+1), slnTextFields[i].getText());
                builder.addParameter
                        ("entcode" + (i+1), "");
                builder.addParameter
                        ("credits" + (i+1), "");
                builder.addParameter
                        ("gr_sys" + (i+1), "");
            }
            return builder.build().toURL();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
