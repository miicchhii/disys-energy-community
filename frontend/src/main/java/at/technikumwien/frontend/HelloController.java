package at.technikumwien.frontend;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.awt.*;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    @FXML
    private TextField currentOverviewTextField;

    @FXML
    protected void onUpdateCurrentOverviewButtonClick(){}

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Text historyText;

    @FXML
    protected void onUpdateHistoryButtonClick() throws InterruptedException {
        historyText.setText("Historie wurde aktualisiert");
        //sleep(1000);
        //historyText.setText("");
    }


}