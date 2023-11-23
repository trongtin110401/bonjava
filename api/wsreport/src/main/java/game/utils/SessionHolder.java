package game.utils;

import org.springframework.stereotype.Component;
import org.yeauty.pojo.Session;

import java.util.ArrayList;

@Component
public class SessionHolder {

    public final static ArrayList<Session> lstSession = new ArrayList<>();

    public void putSession(Session session) {
        lstSession.add(session);
    }

    public ArrayList<Session> getLstSession() {
        return lstSession;
    }
}
