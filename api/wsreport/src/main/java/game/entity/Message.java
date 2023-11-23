package game.entity;


public class Message extends MessageBase {
    String userName;
    String message;
    Object requestBody;

    public Message(String userName, String message) {
        this.userName = userName;
        this.message = message;
    }

    public Message() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(Object requestBody) {
        this.requestBody = requestBody;
    }
}
