package game.handlermanagement;


import game.ws.handler.IWSHandler;
import game.ws.handler.impl.LoginHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class WSHandlerManagement {


    public static HashMap<String, IWSHandler> mapHandler = new HashMap<>();

    public WSHandlerManagement() {
        mapHandler.put("login", new LoginHandler());
    }
}
