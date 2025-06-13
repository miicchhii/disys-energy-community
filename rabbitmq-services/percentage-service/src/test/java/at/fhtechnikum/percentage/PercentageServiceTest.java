package at.fhtechnikum.percentage;

import at.fhtechnikum.shared.EnergyMessage;
import at.technikumwien.database.entity.CurrentEntry;
import at.technikumwien.database.entity.HistoricalEntry;
import at.technikumwien.database.repository.CurrentEntryRepository;
import at.technikumwien.database.repository.HistoricalEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = PercentageService.class)
class PercentageServiceTest {

    @Autowired
    private PercentageService service;

    @MockBean
    private HistoricalEntryRepository historicalRepo;

    @MockBean
    private CurrentEntryRepository currentRepo;

    private final ObjectMapper mapper = new ObjectMapper();
    private final long deliveryTag = 123L;

    @Test
    void whenNoHistoricalEntry_thenAckOnly() throws Exception {
        // arrange
        LocalDateTime now = LocalDateTime.of(2025, 6, 13, 15, 37)
                .truncatedTo(java.time.temporal.ChronoUnit.HOURS);
        EnergyMessage msg = new EnergyMessage("PRODUCER", "COMMUNITY", 1.0,
                now.plusMinutes(37).format(DateTimeFormatter.ISO_DATE_TIME));
        byte[] payload = mapper.writeValueAsBytes(msg);

        when(historicalRepo.findById(now)).thenReturn(Optional.empty());
        Channel channel = mock(Channel.class);

        // act
        service.calculatePercentage(payload, channel, deliveryTag);

        // assert
        verify(currentRepo, never()).save(any());
        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    void whenHistoricalEntryExists_thenCalculateAndSaveAndAck() throws Exception {
        // arrange
        LocalDateTime hour = LocalDateTime.of(2025, 6, 13, 15, 0);
        EnergyMessage msg = new EnergyMessage("PRODUCER", "COMMUNITY", 1.0,
                hour.plusMinutes(37).format(DateTimeFormatter.ISO_DATE_TIME));
        byte[] payload = mapper.writeValueAsBytes(msg);

        HistoricalEntry hist = new HistoricalEntry(hour, 20.0, 10.0, 4.0);
        when(historicalRepo.findById(hour)).thenReturn(Optional.of(hist));
        Channel channel = mock(Channel.class);

        // act
        service.calculatePercentage(payload, channel, deliveryTag);

        // assert
        // depleted = (10/20)*100 = 50, gridPortion = (4/10)*100 = 40
        verify(currentRepo).save(argThat((CurrentEntry e) ->
                e.getHour().equals(hour)
                        && Math.abs(e.getCommunityDepleted() - 50.0) < 1e-6
                        && Math.abs(e.getGridPortion() - 40.0) < 1e-6
        ));
        verify(channel).basicAck(deliveryTag, false);
    }
}
