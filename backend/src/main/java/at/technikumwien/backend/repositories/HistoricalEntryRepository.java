package at.technikumwien.backend.repositories;

import at.technikumwien.backend.model.HistoricalEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricalEntryRepository extends JpaRepository<HistoricalEntry, Long> {

}
