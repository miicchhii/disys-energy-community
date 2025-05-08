package at.technikumwien.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "current_entry")
public class CurrentEntry {
    @Id
    private LocalDateTime hour;

    private double communityDepleted;
    private double gridPortion;

    // Constructors, getters and setters
    public CurrentEntry(LocalDateTime hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    public CurrentEntry() {

    }

    public LocalDateTime getHour() {
        return hour;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }
}
