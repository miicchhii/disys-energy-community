package at.technikumwien.backend.service;

import at.technikumwien.database.repository.CurrentEntryRepository;
import at.technikumwien.database.repository.HistoricalEntryRepository;
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
    private final HistoricalEntryRepository historicalRepo;
    private final CurrentEntryRepository currentRepo;

    public EnergyService(HistoricalEntryRepository historicalRepo, CurrentEntryRepository currentRepo) {
        this.historicalRepo = historicalRepo;
        this.currentRepo = currentRepo;
    }

    public List<CurrentEntry> getCurrentData() {
        return currentRepo.findAll();
    }

    public List<HistoricalEntry> getHistoricalData(LocalDateTime start, LocalDateTime end) {
        return historicalRepo.findByHourBetween(start, end);
    }
}
