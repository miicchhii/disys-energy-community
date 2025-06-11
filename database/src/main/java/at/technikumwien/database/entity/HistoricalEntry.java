package at.technikumwien.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "historical_entry")
public class HistoricalEntry {

    @Id
    private LocalDateTime hour;
    @Column(nullable = false)
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
    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public void setCommunityProduced(double communityProduced) {
        this.communityProduced = communityProduced;
    }

    public void setCommunityUsed(double communityUsed) {
        this.communityUsed = communityUsed;
    }

    public void setGridUsed(double gridUsed) {
        this.gridUsed = gridUsed;
    }

    @Override
    public String toString() {
        return "HistoricalEntry{" +
                "hour=" + hour +
                ", produced=" + communityProduced +
                ", used=" + communityUsed +
                ", grid=" + gridUsed +
                '}';
    }
}
