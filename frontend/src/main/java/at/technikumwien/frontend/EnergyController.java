package at.technikumwien.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class EnergyController {



    @FXML
    private Text currentOverviewTextField;


    // CURRENT DATA
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();



    // API Call
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
                        Platform.runLater(() -> currentOverviewTextField.setText("Error Loading Current Overview"));
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
                Platform.runLater(() -> currentOverviewTextField.setText("no Data"));
            }

        } catch (Exception e) {
            Platform.runLater(() -> currentOverviewTextField.setText("Parsing Error"));
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
    protected void onUpdateHistoryButtonClick() {
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            historyText.setText("Please select a start date and end date.");
            return;
        }

        String start = startDatePicker.getValue().atStartOfDay().toString();
        String end = endDatePicker.getValue().atTime(23, 59).toString();

        String url = String.format("http://localhost:8080/energy/historical?start=%s&end=%s", start, end);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::updateHistoryText)
                    .exceptionally(ex -> {
                        Platform.runLater(() -> historyText.setText("Error loading Data"));
                        return null;
                    });

        } catch (Exception e) {
            e.printStackTrace();
            historyText.setText("Error loading Requests");
        }
    }

    private void updateHistoryText(String json) {
        try {
            EnergyHistoricalData[] dataArray = objectMapper.readValue(json, EnergyHistoricalData[].class);

            double totalProduced = 0;
            double totalUsed = 0;
            double totalGrid = 0;

            for (EnergyHistoricalData entry : dataArray) {
                totalProduced += entry.getCommunityProduced();
                totalUsed += entry.getCommunityUsed();
                totalGrid += entry.getGridUsed();
            }

            String result = String.format(
                    "Community produced: %.3f kWh\n" +
                            "Community used: %.3f kWh\n" +
                            "Grid used: %.3f kWh",
                    totalProduced, totalUsed, totalGrid
            );

            Platform.runLater(() -> historyText.setText(result));

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> historyText.setText("Parsing-Error"));
        }
    }




}