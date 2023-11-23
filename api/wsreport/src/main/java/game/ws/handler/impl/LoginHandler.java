package game.ws.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;

import game.bean.MapperUtils;
import game.entity.Message;
import game.entity.impl.LoginRequestBody;
import game.entity.response.WSLoginResponse;
import game.ws.handler.IWSHandler;
import game.ws.handler.WSHandler;
import org.yeauty.pojo.Session;

public class LoginHandler extends WSHandler implements IWSHandler {


    @Override
    public void handleMessage(Session session, Message message) throws JsonProcessingException {
        LoginRequestBody loginRequestBody = MapperUtils.mapper.convertValue(message.getRequestBody(), LoginRequestBody.class);
        session.sendText("helllo");
        this.sendMessage(session, new WSLoginResponse());
    }
}
