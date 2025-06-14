package at.technikumwien.backend.repository;

import at.technikumwien.database.entity.CurrentEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface CurrentEntryRepository extends JpaRepository<CurrentEntry, LocalDateTime> {
}
