package game.ws.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;

import game.entity.Message;
import game.entity.response.WSLoginResponse;
import game.ws.handler.IWSHandler;
import game.ws.handler.WSHandler;
import org.yeauty.pojo.Session;

public class TaiXiuHandler extends WSHandler implements IWSHandler {

    @Override
    public void handleMessage(Session session, Message message) throws JsonProcessingException { // todo : viết hàm xử lý theo commnd

        this.sendMessage(session, new WSLoginResponse());
    }
}
