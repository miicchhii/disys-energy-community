package at.technikumwien.user;

import at.fhtechnikum.shared.EnergyMessage;
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
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EnergyUser {
    private static final String QUEUE_NAME = "energy.input";
    private static final String RABBITMQ_HOST = System.getenv().getOrDefault("RABBITMQ_HOST", "localhost");

    private final ConnectionFactory factory;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();
    private final DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    EnergyMessage msg = new EnergyMessage();
    public EnergyUser() {
        factory = new ConnectionFactory();
        factory.setHost(RABBITMQ_HOST);
    }

    public void start() throws Exception {
        try (Connection conn = factory.newConnection();
             Channel channel = conn.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            int num_demo_messages = 29000;
            int counter = 0;
            while (counter < num_demo_messages) {

                LocalDateTime now = LocalDateTime.now();
                double kwh = generateUsage(now.getHour());
                String timestamp = LocalDateTime.now().minusDays(20).plusSeconds(counter* 60L).format(fmt);

                msg.setType("USER");
                msg.setAssociation("COMMUNITY");
                msg.setKwh(kwh);
                msg.setDatetime(timestamp);


                byte[] body = mapper.writeValueAsBytes(msg);
                channel.basicPublish("", QUEUE_NAME, null, body);

                System.out.printf("[User] Sent -> kWh: %s | Timestamp: %s%n", msg.getKwh(), msg.getDatetime());
                counter++;
                //TimeUnit.SECONDS.sleep(5);
            }
        }
    }

    private double generateUsage(int hour) {
        // Spitzenlast morgens (6-9) und abends (17-20)
        if ((hour >= 6 && hour < 10) || (hour >= 17 && hour < 21)) {
            // zwischen 0.002 und 0.005 kWh
            return 0.002 + random.nextDouble() * 0.003;
        }
        // sonst zwischen 0.0005 und 0.0015 kWh
        return 0.0005 + random.nextDouble() * 0.001;
    }

    public static void main(String[] args) {
        try {
            new EnergyUser().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}