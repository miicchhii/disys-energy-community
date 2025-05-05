package at.technikumwien.backend.repositories;

import at.technikumwien.backend.model.CurrentEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Repository;
@Repository
public interface CurrentEntryRepository extends JpaRepository<CurrentEntry, LocalDateTime> {
}