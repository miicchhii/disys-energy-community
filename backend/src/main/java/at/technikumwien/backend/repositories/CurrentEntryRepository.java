package at.technikumwien.backend.repositories;

import at.technikumwien.backend.model.CurrentEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrentEntryRepository extends JpaRepository<CurrentEntry, Long> {
    // You can add custom query methods here if needed
}
