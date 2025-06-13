package at.technikumwien.backend.service;

import at.technikumwien.database.entity.CurrentEntry;
import at.technikumwien.database.entity.HistoricalEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EnergyServiceUnitTest {

    private EnergyService energyService;

    @BeforeEach
    void setUp() {
        // Mit Demo-Daten vom 2025-01-10 über 5 Tage
        energyService = new EnergyService();
    }

    @Test
    void getCurrentData_containsSingleDemoEntry() {
        List<CurrentEntry> current = energyService.getCurrentData();

        // Im Service ist genau eine feste CurrentEntry vordefiniert
        assertThat(current).hasSize(1);
        CurrentEntry e = current.get(0);
        assertThat(e.getHour()).isEqualTo(LocalDateTime.parse("2025-01-10T14:00:00"));
        assertThat(e.getCommunityDepleted()).isEqualTo(100.00);
        assertThat(e.getGridPortion()).isEqualTo(5.63);
    }

    @Test
    void getHistoricalData_filtersByStartAndEnd() {
        LocalDateTime start = LocalDateTime.parse("2025-01-11T00:00:00");
        LocalDateTime end   = LocalDateTime.parse("2025-01-12T23:00:00");

        List<HistoricalEntry> hist = energyService.getHistoricalData(start, end);

        // 2 volle Tage à 24 Stunden
        assertThat(hist).hasSize(48);

        // Erster und letzter Timestamp stimmen
        assertThat(hist.get(0).getHour()).isEqualTo(start);
        assertThat(hist.get(hist.size()-1).getHour()).isEqualTo(end);
    }

    @Test
    void generateDemoDataRange_createsCorrectCount() throws Exception {
        // Über Reflection auf private Methode zugreifen (optional)
        var method = EnergyService.class.getDeclaredMethod("generateDemoDataRange", LocalDate.class, int.class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<HistoricalEntry> allData = (List<HistoricalEntry>) method.invoke(energyService, LocalDate.of(2025,1,10), 3);

        // 3 Tage * 24 Stunden
        assertThat(allData).hasSize(3 * 24);
    }

    @Test
    void round_shouldRoundToThreeDecimals() throws Exception {
        var method = EnergyService.class.getDeclaredMethod("round", double.class);
        method.setAccessible(true);
        double rounded = (double) method.invoke(energyService, 2.34567);
        assertThat(rounded).isEqualTo(2.346);
    }
}
