package game.ws.handler;

import com.fasterxml.jackson.core.JsonProcessingException;

import game.bean.MapperUtils;
import game.entity.IResponse;
import org.yeauty.pojo.Session;

import java.util.HashMap;

public abstract class WSHandler {

    static HashMap<Session, String> listUser = new HashMap<>();

    public void subscribe(Session session, String user) {
        listUser.put(session, user);
    }

    public void sendMessage(Session session, IResponse response) throws JsonProcessingException {

        String json = MapperUtils.mapper.writeValueAsString(response);
        session.sendText(json);

    }
}
