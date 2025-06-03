package at.fhtechnikum.usage;

import at.fhtechnikum.shared.EnergyMessage;
import at.technikumwien.database.entity.HistoricalEntry;
import at.technikumwien.database.repository.HistoricalEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Service
public class UsageService {



    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private HistoricalEntryRepository repository;

    @RabbitListener(queues = "energy.input", ackMode = "MANUAL")
    public void processEnergy(@Payload byte[] messageBytes,
                              Channel channel,
                              @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        String jsonString = new String(messageBytes, StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        EnergyMessage msg = objectMapper.readValue(jsonString, EnergyMessage.class);

        System.out.println("=== USAGE SERVICE ===");
        System.out.printf("Received: %s | Assoc: %s | kWh: %.3f | datetime: %s%n",
                msg.getType(), msg.getAssociation(), msg.getKwh(), msg.getDatetime());

        String datetime = msg.getDatetime();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime parsed = LocalDateTime.parse(datetime, formatter);
        LocalDateTime hour = parsed.truncatedTo(ChronoUnit.HOURS);


        HistoricalEntry entry = repository.findById(hour).orElse(new HistoricalEntry(hour, 0, 0, 0));


        switch (msg.getType()) {
            case "PRODUCER" -> {
                if ("COMMUNITY".equals(msg.getAssociation())) {
                    entry.setCommunityProduced(entry.getCommunityProduced() + msg.getKwh());
                }
            }
            case "USER" -> {
                if ("COMMUNITY".equals(msg.getAssociation())) {
                    double producedAvailable = entry.getCommunityProduced() - entry.getCommunityUsed();
                    double fromCommunity = Math.min(msg.getKwh(), producedAvailable);
                    double fromGrid = msg.getKwh() - fromCommunity;

                    entry.setCommunityUsed(entry.getCommunityUsed() + fromCommunity);
                    entry.setGridUsed(entry.getGridUsed() + fromGrid);
                } else if ("GRID".equals(msg.getAssociation())) {
                    entry.setGridUsed(entry.getGridUsed() + msg.getKwh());
                }
            }
        }

        repository.save(entry);
        channel.basicAck(deliveryTag, false);
        System.out.println("Updated DB entry: " + entry);
        System.out.println("=======================");
    }
}
