package at.technikumwien.backend.service;

import at.technikumwien.database.entity.CurrentEntry;
import at.technikumwien.database.entity.HistoricalEntry;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnergyService {

    private final List<HistoricalEntry> historicalData;

    public EnergyService() {
        this.historicalData = generateDemoDataRange(LocalDate.of(2025, 1, 10), 5); // 3 days
    }

    private final List<CurrentEntry> currentData = List.of(
            new CurrentEntry(LocalDateTime.parse("2025-01-10T14:00:00"), 100.00, 5.63)
    );

    public List<CurrentEntry> getCurrentData() {
        return currentData;
    }

    public List<HistoricalEntry> getHistoricalData(LocalDateTime start, LocalDateTime end) {
        return historicalData.stream()
                .filter(entry -> !entry.getHour().isBefore(start) && !entry.getHour().isAfter(end))
                .collect(Collectors.toList());
    }



    private List<HistoricalEntry> generateDemoDataRange(LocalDate startDate, int days) {
        List<HistoricalEntry> allData = new ArrayList<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            allData.addAll(generateDemoDataForDay(date));
        }
        return allData;
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

            data.add(new HistoricalEntry(time, round(communityProduced), round(communityUsed), round(gridUsed)));
        }

        return data;
    }

    // Rounds to 3 decimal places
    private double round(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }


}
