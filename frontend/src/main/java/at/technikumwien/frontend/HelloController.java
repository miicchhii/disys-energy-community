package at.technikumwien.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import javafx.scene.control.TextField;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }


    @FXML
    private Text currentOverviewTextField;


    // CURRENT DATA
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();



    // HIER passiert der API-Call beim Button-Klick
    @FXML
    protected void onUpdateCurrentOverviewButtonClick() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/energy/current"))
                    .GET()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::updateCurrentOverviewTextField)
                    .exceptionally(ex -> {
                        ex.printStackTrace();
                        Platform.runLater(() -> currentOverviewTextField.setText("Fehler beim Laden"));
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateCurrentOverviewTextField(String json) {
        try {
            EnergyCurrentData[] dataArray = objectMapper.readValue(json, EnergyCurrentData[].class);

            if (dataArray.length > 0) {
                EnergyCurrentData data = dataArray[0];

                String text = String.format("Community Pool: %.2f %%\nGrid Portion: %.2f %%",
                        data.getCommunityDepleted(), data.getGridPortion());

                Platform.runLater(() -> currentOverviewTextField.setText(text));
            } else {
                Platform.runLater(() -> currentOverviewTextField.setText("Keine Daten"));
            }

        } catch (Exception e) {
            Platform.runLater(() -> currentOverviewTextField.setText("Parsing-Fehler"));
        }
    }





    // HISTORICAL DATA

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