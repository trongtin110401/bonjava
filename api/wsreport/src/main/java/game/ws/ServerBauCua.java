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
        path = "/baucua",
        corsOrigins = "*"
) // đoạn này nó phải khác nhau nhé ko confflict
// e đang dùng path là ws , a dung path khác đi , dạng /taixiu hay gì đó là đc
public class ServerBauCua {

    static HashMap<String, Session> mapUser = new HashMap<>();
    static HashMap<Session, String> mapSessionUser = new HashMap<>();
    public static ArrayList<Session> sessions = new ArrayList<>();
    @Autowired
    private WSHandlerManagement wsHandlerManagement;

    @BeforeHandshake
    public void handshake(Session session) {
        sessions.add(session);
    }

    @OnOpen
    public void onOpen(Session session) {

    }

    @OnClose
    public void onClose(Session session) throws IOException {
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
    }


    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
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
