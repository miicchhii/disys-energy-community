package at.technikumwien.backend.service;

import at.technikumwien.database.entity.CurrentEntry;
import at.technikumwien.database.entity.HistoricalEntry;
import at.technikumwien.database.repository.CurrentEntryRepository;
import at.technikumwien.database.repository.HistoricalEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnergyServiceUnitTest {

    @Mock
    private HistoricalEntryRepository historicalRepo;

    @Mock
    private CurrentEntryRepository currentRepo;

    @InjectMocks
    private EnergyService energyService;

    private CurrentEntry sampleCurrent;
    private HistoricalEntry sampleHistorical;

    @BeforeEach
    void setUp() {
        // Beispiel‐Daten für CurrentEntry
        sampleCurrent = new CurrentEntry();
        sampleCurrent.setHour(LocalDateTime.of(2025, 1, 1, 12, 0));
        sampleCurrent.setCommunityDepleted(123.45);
        sampleCurrent.setGridPortion(67.89);

        // Beispiel‐Daten für HistoricalEntry
        sampleHistorical = new HistoricalEntry();
        sampleHistorical.setHour(LocalDateTime.of(2025, 1, 1, 1, 0));
        // statt setValue(...) nutzen wir den vorhandenen Setter
        sampleHistorical.setCommunityProduced(42.0);
        sampleHistorical.setCommunityUsed(0.0);
        sampleHistorical.setGridUsed(0.0);
    }

    @Test
    void getCurrentData_returnsRepoData() {
        when(currentRepo.findAll()).thenReturn(List.of(sampleCurrent));

        List<CurrentEntry> result = energyService.getCurrentData();

        assertThat(result)
                .hasSize(1)
                .containsExactly(sampleCurrent);

        verify(currentRepo).findAll();
    }

    @Test
    void getHistoricalData_returnsRepoDataBetween() {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 0, 0);
        LocalDateTime end   = LocalDateTime.of(2025, 1, 1, 23, 59);

        when(historicalRepo.findByHourBetween(start, end))
                .thenReturn(List.of(sampleHistorical));

        List<HistoricalEntry> result = energyService.getHistoricalData(start, end);

        assertThat(result)
                .hasSize(1)
                .containsExactly(sampleHistorical);

        verify(historicalRepo).findByHourBetween(start, end);
    }
}
