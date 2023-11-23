package game.entity;


public class MessageBase {
    String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public MessageBase(String type) {
        this.type = type;
    }

    public MessageBase() {
    }
}
