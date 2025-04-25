package at.technikumwien.backend.model;

import java.time.LocalDateTime;

public class CurrentEntry {
    private LocalDateTime hour;
    private double communityDepleted;
    private double gridPortion;

    // Constructors, getters and setters
    public CurrentEntry(LocalDateTime hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
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
