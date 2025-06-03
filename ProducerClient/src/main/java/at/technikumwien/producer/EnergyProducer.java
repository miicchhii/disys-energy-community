package at.technikumwien.producer;
import java.util.Locale;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EnergyProducer {
    private static final String QUEUE_NAME = "energy.input";
    private static final String RABBITMQ_HOST = System.getenv().getOrDefault("RABBITMQ_HOST", "localhost");
    private static final double LATITUDE = Double.parseDouble(
            System.getenv().getOrDefault("LATITUDE", "47.3769")
    );
    private static final double LONGITUDE = Double.parseDouble(
            System.getenv().getOrDefault("LONGITUDE", "8.5417")
    );

    private final ConnectionFactory factory;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();
    private final HttpClient http = HttpClient.newHttpClient();
    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public EnergyProducer() {
        factory = new ConnectionFactory();
        factory.setHost(RABBITMQ_HOST);
    }

    public void start() throws Exception {
        try (Connection conn = factory.newConnection();
             Channel channel = conn.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            while (true) {
                double weatherFactor = fetchWeatherFactor();
                // Basisproduktion zwischen 0.001 und 0.005 kWh
                double base = 0.001 + random.nextDouble() * 0.004;
                double kwh = base * weatherFactor;
                String timestamp = LocalDateTime.now().format(fmt);

                ObjectNode msg = mapper.createObjectNode();
                msg.put("type", "PRODUCER");
                msg.put("association", "COMMUNITY");
                msg.put("kwh", String.format(Locale.US,"%.6f", kwh));
                msg.put("datetime", timestamp);

                byte[] body = mapper.writeValueAsBytes(msg);
                channel.basicPublish("", QUEUE_NAME, null, body);

                System.out.printf("[Producer] Sent: %s%n", msg.toString());
                TimeUnit.SECONDS.sleep(5);
            }
        }
    }

    private double fetchWeatherFactor() {
        try {
            String url = String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current_weather=true",
                    LATITUDE, LONGITUDE
            );
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<InputStream> resp = http.send(req, HttpResponse.BodyHandlers.ofInputStream());
            var node = mapper.readTree(resp.body()).path("current_weather");
            int code = node.path("weathercode").asInt();
            // weathercode 0 = klar, 1–3 gering bewölkt,  >50 Regen etc.
            if (code == 0) return 1.5;
            if (code <= 3) return 1.2;
            return 0.8;
        } catch (Exception e) {
            System.err.println("Fehler beim Wetterabruf, nutze Faktor 1.0: " + e.getMessage());
            return 1.0;
        }
    }

    public static void main(String[] args) {
        try {
            new EnergyProducer().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
