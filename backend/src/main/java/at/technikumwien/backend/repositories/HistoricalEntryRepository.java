package at.technikumwien.backend.repositories;

import at.technikumwien.backend.model.HistoricalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricalEntryRepository
        extends JpaRepository<HistoricalEntry, LocalDateTime> {
    List<HistoricalEntry> findByHourBetween(LocalDateTime start, LocalDateTime end);
}
