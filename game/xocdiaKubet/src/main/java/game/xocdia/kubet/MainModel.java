package game.xocdia.kubet;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MainModel {

    @JsonProperty("C")
    private String c;

    @JsonProperty("M")
    private List<Message> m;

    // Getters and Setters
    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public List<Message> getM() {
        return m;
    }

    public void setM(List<Message> m) {
        this.m = m;
    }

}
