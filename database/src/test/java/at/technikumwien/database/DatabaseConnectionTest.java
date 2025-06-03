package at.technikumwien.database;

import at.technikumwien.database.entity.CurrentEntry;
import at.technikumwien.database.entity.HistoricalEntry;
import at.technikumwien.database.repository.CurrentEntryRepository;
import at.technikumwien.database.repository.HistoricalEntryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DatabaseConnectionTest {

    @Autowired
    private CurrentEntryRepository currentRepo;

    @Autowired
    private HistoricalEntryRepository historicalRepo;

    @Test
    void testCurrentEntryPersistence() {
        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        CurrentEntry entry = new CurrentEntry();
        entry.setHour(now);
        entry.setCommunityDepleted(100.0);
        entry.setGridPortion(5.6);

        currentRepo.save(entry);

        CurrentEntry loaded = currentRepo.findById(now).orElseThrow();
        assertThat(loaded.getGridPortion()).isEqualTo(5.6);
    }

    @Test
    void testHistoricalEntryPersistence() {
        LocalDateTime hour = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).minusHours(1);
        HistoricalEntry entry = new HistoricalEntry();
        entry.setHour(hour);
        entry.setCommunityProduced(20.0);
        entry.setCommunityUsed(18.0);
        entry.setGridUsed(2.5);

        historicalRepo.save(entry);

        HistoricalEntry found = (HistoricalEntry) historicalRepo.findById(hour).orElseThrow();
        assertThat(found.getCommunityUsed()).isEqualTo(18.0);
    }
}
