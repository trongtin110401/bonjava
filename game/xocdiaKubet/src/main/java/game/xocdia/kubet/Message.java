package game.xocdia.kubet;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Message {

    @JsonProperty("H")
    private String h; // Hub name

    @JsonProperty("M")
    private String m; // Method name

    @JsonProperty("A")
    private List<Object> a; // Arguments list

    // Getters and Setters
    public String getH() {
        return h;
    }

    public void setH(String h) {
        this.h = h;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public List<Object> getA() {
        return a;
    }

    public void setA(List<Object> a) {
        this.a = a;
    }

}
