package game.entity.impl;


import game.entity.IWSRequestBody;

public class LoginRequestBody implements IWSRequestBody {
    String username;
    String pass;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public LoginRequestBody(String username, String pass) {
        this.username = username;
        this.pass = pass;
    }

}
