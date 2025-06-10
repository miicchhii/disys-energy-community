package at.technikumwien.backend.controller;

import at.technikumwien.database.entity.CurrentEntry;
import at.technikumwien.database.entity.HistoricalEntry;
import at.technikumwien.backend.service.EnergyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/energy")
public class EnergyController {

    private final EnergyService energyService;

    public EnergyController(EnergyService energyService) {
        this.energyService = energyService;
    }

    @GetMapping("/current")
    public List<CurrentEntry> getCurrent() {
        // Return the communityDepleted value as the "percentage of current hour"
        return energyService.getCurrentData();
    }

    @GetMapping("/historical")
    public List<HistoricalEntry> getHistorical(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return energyService.getHistoricalData(start, end);
    }
}
