package at.fhtechnikum.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class EnergyMessage implements Serializable {
    private String type;          // "PRODUCER" oder "USER"
    private String association;   // immer "COMMUNITY"
    private double kwh;           // z.B. 0.003
    private String datetime;      // ISO String, z.B. "2025-01-10T14:33:00"

    @JsonCreator
    public EnergyMessage(
            @JsonProperty("type") String type,
            @JsonProperty("association") String association,
            @JsonProperty("kwh") double kwh,
            @JsonProperty("datetime") String datetime) {
        this.type = type;
        this.association = association;
        this.kwh = kwh;
        this.datetime = datetime;
    }

    public EnergyMessage() {
        // Default-Konstruktor für Deserialisierung
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("association")
    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    @JsonProperty("kwh")
    public double getKwh() {
        return kwh;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    @JsonProperty("datetime")
    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}

