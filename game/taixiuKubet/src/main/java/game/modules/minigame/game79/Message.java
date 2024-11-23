package game.modules.minigame.game79;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Message {
    @JsonProperty("H")
    private String hub;

    @JsonProperty("M")
    private String method;

    @JsonProperty("A")
    private List<Object> arguments;

    public String getHub() {
        return hub;
    }

    public void setHub(String hub) {
        this.hub = hub;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public void setArguments(List<Object> arguments) {
        this.arguments = arguments;
    }
}
