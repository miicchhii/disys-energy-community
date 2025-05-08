package at.technikumwien.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "historical_entry")
public class HistoricalEntry {

    @Id
    private LocalDateTime hour;

    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    // Constructors, getters and setters
    public HistoricalEntry(LocalDateTime hour, double communityProduced, double communityUsed, double gridUsed) {
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public HistoricalEntry() {

    }

    public LocalDateTime getHour() {
        return hour;
    }

    public double getCommunityProduced() {
        return communityProduced;
    }

    public double getCommunityUsed() {
        return communityUsed;
    }

    public double getGridUsed() {
        return gridUsed;
    }
}
