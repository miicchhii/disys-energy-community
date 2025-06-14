package at.technikumwien.backend.repository;

import at.technikumwien.database.entity.HistoricalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HistoricalEntryRepository extends JpaRepository<HistoricalEntry, LocalDateTime> {
    List<HistoricalEntry> findByHourBetween(LocalDateTime start, LocalDateTime end);
}
