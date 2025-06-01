package at.fhtechnikum.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class EchoMessage {
    private String message;

    @JsonCreator
    public EchoMessage(@JsonProperty("message") String msg) {
        this.message = msg;
    }

    @JsonProperty("message")
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
