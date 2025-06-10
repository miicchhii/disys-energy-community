package at.fhtechnikum.percentage;

import at.fhtechnikum.shared.EnergyMessage;
import at.technikumwien.database.entity.CurrentEntry;
import at.technikumwien.database.entity.HistoricalEntry;
import at.technikumwien.database.repository.CurrentEntryRepository;
import at.technikumwien.database.repository.HistoricalEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
public class PercentageService {

    @Autowired
    private HistoricalEntryRepository historicalRepo;

    @Autowired
    private CurrentEntryRepository currentRepo;

    @RabbitListener(queues = "energy.input", ackMode = "MANUAL")
    public void calculatePercentage(@Payload byte[] messageBytes,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        String json = new String(messageBytes, StandardCharsets.UTF_8);
        ObjectMapper mapper = new ObjectMapper();
        EnergyMessage msg = mapper.readValue(json, EnergyMessage.class);

        String datetime = msg.getDatetime();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime parsed = LocalDateTime.parse(datetime, formatter);
        LocalDateTime hour = parsed.truncatedTo(ChronoUnit.HOURS);

        // Lade aktuelle Summen aus historical_entry
        HistoricalEntry entry = historicalRepo.findById(hour).orElse(null);
        if (entry == null) {
            channel.basicAck(tag, false);
            return; // Noch keine Daten für diese Stunde
        }

        double produced = entry.getCommunityProduced();
        double used = entry.getCommunityUsed();
        double gridUsed = entry.getGridUsed();

        double depleted = Math.min(100.0, (produced == 0) ? 100.0 : (used / produced) * 100.0);
        double gridPortion = Math.min(100.0, (used == 0) ? 0.0 : (gridUsed / used) * 100.0);

        CurrentEntry currentEntry = new CurrentEntry(hour, depleted, gridPortion);
        currentRepo.save(currentEntry);

        System.out.println("=== PERCENTAGE SERVICE ===");
        System.out.printf("Hour: %s | Depleted: %.2f%% | Grid: %.2f%%%n",
                hour, depleted, gridPortion);
        System.out.println("==========================");

        channel.basicAck(tag, false);
    }
}
