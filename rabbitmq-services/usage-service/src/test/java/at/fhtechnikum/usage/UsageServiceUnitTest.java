package at.fhtechnikum.usage;

import at.fhtechnikum.shared.EnergyMessage;
import at.technikumwien.database.entity.HistoricalEntry;
import at.technikumwien.database.repository.HistoricalEntryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsageServiceTest {

    @Mock
    private HistoricalEntryRepository repository;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Channel channel;

    @InjectMocks
    private UsageService service;

    private ObjectMapper mapper;
    private final long deliveryTag = 42L;

    @BeforeEach
    void setUp() {
        mapper = new ObjectMapper();
    }

    @Test
    void producerMessage_createsAndSavesNewEntry_andForwardsAndAcks() throws Exception {
        // Arrange: PRODUCER community for a new hour
        LocalDateTime dt = LocalDateTime.of(2025,6,13,10,15);
        String iso = dt.format(DateTimeFormatter.ISO_DATE_TIME);
        EnergyMessage msg = new EnergyMessage("PRODUCER","COMMUNITY", 7.5, iso);
        byte[] payload = mapper.writeValueAsBytes(msg);

        when(repository.findById(dt.truncatedTo(ChronoUnit.HOURS)))
                .thenReturn(Optional.empty());

        // Act
        service.processEnergy(payload, channel, deliveryTag);

        // Assert: repository.save called with a new entry having communityProduced=7.5
        ArgumentCaptor<HistoricalEntry> captor = ArgumentCaptor.forClass(HistoricalEntry.class);
        verify(repository).save(captor.capture());
        HistoricalEntry saved = captor.getValue();
        assert saved.getHour().equals(dt.truncatedTo(ChronoUnit.HOURS));
        assert saved.getCommunityProduced() == 7.5;
        assert saved.getCommunityUsed() == 0.0;
        assert saved.getGridUsed() == 0.0;

        // forwarded and acked (Feldgleichheit prüfen)
        verify(rabbitTemplate).convertAndSend(
                eq("energy.processing.exchange"),
                eq("energy.output"),
                refEq(msg)
        );
        verify(channel).basicAck(deliveryTag, false);
    }

    @Test
    void userCommunityMessage_updatesExistingEntry_correctlyAndForwardsAndAcks() throws Exception {
        // Arrange: USER COMMUNITY event
        LocalDateTime hour = LocalDateTime.of(2025,6,13,11,0);
        String iso = hour.plusMinutes(30).format(DateTimeFormatter.ISO_DATE_TIME);
        EnergyMessage msg = new EnergyMessage("USER","COMMUNITY", 10.0, iso);
        byte[] payload = mapper.writeValueAsBytes(msg);

        // existing entry: produced=8, used=3, gridUsed=1
        HistoricalEntry existing = new HistoricalEntry(hour, 8.0, 3.0, 1.0);
        when(repository.findById(hour)).thenReturn(Optional.of(existing));

        // Act
        service.processEnergy(payload, channel, deliveryTag);

        // Assert
        // producedAvailable = 8-3 = 5; fromCommunity = min(10,5)=5; fromGrid=5
        assert existing.getCommunityUsed() == 3.0 + 5.0;
        assert existing.getGridUsed() == 1.0 + 5.0;
        verify(repository).save(existing);
        verify(rabbitTemplate).convertAndSend(
                eq("energy.processing.exchange"),
                eq("energy.output"),
                refEq(msg)
        );
        verify(channel).basicAck(deliveryTag,false);
    }

    @Test
    void userGridMessage_appendsGridUsedAndForwardsAndAcks() throws Exception {
        // Arrange: USER GRID event
        LocalDateTime hour = LocalDateTime.of(2025,6,13,12,0);
        String iso = hour.plusMinutes(45).format(DateTimeFormatter.ISO_DATE_TIME);
        EnergyMessage msg = new EnergyMessage("USER","GRID", 4.0, iso);
        byte[] payload = mapper.writeValueAsBytes(msg);

        HistoricalEntry existing = new HistoricalEntry(hour, 5.0, 2.0, 1.0);
        when(repository.findById(hour)).thenReturn(Optional.of(existing));

        // Act
        service.processEnergy(payload, channel, deliveryTag);

        // Assert: only gridUsed increased by 4.0
        assert existing.getGridUsed() == 1.0 + 4.0;
        verify(repository).save(existing);
        verify(rabbitTemplate).convertAndSend(
                eq("energy.processing.exchange"),
                eq("energy.output"),
                refEq(msg)
        );
        verify(channel).basicAck(deliveryTag,false);
    }
}
