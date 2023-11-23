package game.ws.handler;

import com.fasterxml.jackson.core.JsonProcessingException;

import game.entity.Message;
import org.yeauty.pojo.Session;

public interface IWSHandler {

    void handleMessage(Session session, Message message) throws JsonProcessingException;

}
