package at.technikumwien.backend.service;

import at.technikumwien.backend.model.CurrentEntry;
import at.technikumwien.backend.model.HistoricalEntry;
import at.technikumwien.backend.repositories.CurrentEntryRepository;
import at.technikumwien.backend.repositories.HistoricalEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnergyService {

    private final HistoricalEntryRepository histRepo;
    private final CurrentEntryRepository currRepo;

    public EnergyService(HistoricalEntryRepository histRepo, CurrentEntryRepository currRepo) {
        this.histRepo = histRepo;
        this.currRepo = currRepo;
        // Demo-Daten erzeugen und persistieren
        seedHistorical(LocalDate.of(2025,1,10), 5);
        seedCurrent();
    }

    //  historische Daten abrufen
    public List<HistoricalEntry> getHistoricalData(LocalDateTime start, LocalDateTime end) {
        return histRepo.findByHourBetween(start, end);
    }

    //  aktuelle Daten abrufen
    public List<CurrentEntry> getCurrentData() {
        return currRepo.findAll();
    }

    // Hilfsmethode: seed Historical
    private void seedHistorical(LocalDate startDate, int days) {
        histRepo.deleteAll();
        for (int d = 0; d < days; d++) {
            LocalDate day = startDate.plusDays(d);
            histRepo.saveAll(generateDemoDataForDay(day));
        }
    }

    // Hilfsmethode: seed Current
    private void seedCurrent() {
        currRepo.deleteAll();
        CurrentEntry e = new CurrentEntry(
                LocalDateTime.parse("2025-01-10T14:00:00"),
                100.00, 5.63
        );
        currRepo.save(e);
    }

    private List<HistoricalEntry> generateDemoDataForDay(LocalDate date) {
        List<HistoricalEntry> data = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime time = date.atTime(hour, 0);

            // Simulated production values
            double solar = Math.max(0, 10 * Math.sin((Math.PI / 12) * (hour - 6))); // peaks at noon
            double wind = 2 + Math.random() * 3; // constant-ish but fluctuating
            double communityProduced = solar + wind;

            // Simulated usage values
            double baseUsage = 5 + Math.random() * 2;
            double peakMorning = (hour >= 6 && hour <= 9) ? 5 : 0;
            double peakEvening = (hour >= 17 && hour <= 21) ? 6 : 0;
            double communityUsed = baseUsage + peakMorning + peakEvening;

            // Grid used only if community can't meet demand
            double gridUsed = Math.max(0, communityUsed - communityProduced);

            data.add(new HistoricalEntry(
                    time,
                    round(communityProduced),
                    round(communityUsed),
                    round(gridUsed)
            ));
        }
        return data;
    }


    private double round(double v) {
        return Math.round(v * 1000.0) / 1000.0;
    }
}
