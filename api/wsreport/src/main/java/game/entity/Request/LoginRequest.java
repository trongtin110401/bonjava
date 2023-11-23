package game.entity.Request;


import game.entity.MessageBase;
import game.entity.impl.LoginRequestBody;

public class LoginRequest extends MessageBase {
    String userName;
    String message;
    LoginRequestBody requestBody;

    public LoginRequest(String userName, String message) {
        this.userName = userName;
        this.message = message;
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

    public LoginRequestBody getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(LoginRequestBody requestBody) {
        this.requestBody = requestBody;
    }
}
