package game.ws.enity;


import org.yeauty.pojo.Session;

import java.util.Objects;

public class UserConnect {
    private String username;
    private Session session;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserConnect that = (UserConnect) o;
        return username.equals(that.username);
    }

    public UserConnect() {
    }

    public UserConnect(String username, Session session) {
        this.username = username;
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }
}
