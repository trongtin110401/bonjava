package game.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import game.entity.Message;
import game.handlermanagement.WSHandlerManagement;
import io.netty.handler.timeout.IdleStateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Component
@ServerEndpoint(host = "${ws.host}",
        port = "${ws.port}",
        path = "/state-report",
        corsOrigins = "*"
)
public class ServerStateGame {

    static HashMap<String, Session> mapUser = new HashMap<>();
    static HashMap<Session, String> mapSessionUser = new HashMap<>();
    public static ArrayList<Session> sessions = new ArrayList<>();
    @Autowired
    private WSHandlerManagement wsHandlerManagement;

    @BeforeHandshake
    public void handshake(Session session) {

        // todo: add to hashmap
        //session.setSubprotocols("stomp");
        sessions.add(session);
    }

    @OnOpen
    public void onOpen(Session session) {

    }

    @OnClose
    public void onClose(Session session) throws IOException {
        // CacheService cacheService = new CacheServiceImpl();
        // remove session connect
        // mapUser.remove()
        sessions.remove(session);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        throwable.printStackTrace();
    }


    @OnMessage
    public void onMessage(Session session, String message) throws IOException {


        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        Message mess = new ObjectMapper().readValue(message, Message.class);
//        LoginRequestBody loginRequestBody = null;
//        if (mess.getType().equals("login")) {
//            //loginRequestBody =  new ObjectMapper().readValue(message, (JavaType) mess.getRequestBody());
//            loginRequestBody = new ObjectMapper().convertValue(mess.getRequestBody(), LoginRequestBody.class);
//        }


        //     wsHandlerManagement.mapHandler.get(mess.getType()).handleMessage(session, mess);
//        System.out.println(mapSessionUser.keySet().toString());
//        for (Session userSession : mapSessionUser.keySet()) {
//            Message mess1 = new Message(mapSessionUser.get(session), message);
//            mess1.setRequestBody(loginRequestBody);
//            String json = ow.writeValueAsString(mess1);
//            userSession.sendText(json);
//        }


    }


    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        for (byte b : bytes) {
            System.out.println(b);
        }
        session.sendBinary(bytes);
    }

    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    break;
                case WRITER_IDLE:
                    break;
                case ALL_IDLE:
                    break;
                default:
                    break;
            }
        }
    }
}
