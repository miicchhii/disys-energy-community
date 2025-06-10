package at.technikumwien.database.repository;

import at.technikumwien.database.entity.HistoricalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface HistoricalEntryRepository extends JpaRepository<HistoricalEntry, LocalDateTime> {

}
