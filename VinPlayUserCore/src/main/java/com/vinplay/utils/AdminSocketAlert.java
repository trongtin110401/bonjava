package com.vinplay.utils;


import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;
import java.util.HashMap;

public class AdminSocketAlert {

    private static Socket _instance;

    public static Socket getInstance() {
        if (_instance == null) {
            try {
                _instance = IO.socket("http://10.40.112.3:9092");

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            _instance.connect();
        }
        return _instance;
    }

    public static void sendMessageAlert(String eventName,String userName, String JsonMessage) {
        HashMap<String, String> user = new HashMap<>();
        user.put("userName", userName);
        user.put("message", JsonMessage);

        AdminSocketAlert.getInstance().emit(eventName, user);

    }

    public static void main(String[] args) {
        int i = 0 ;
        while (i <10 ){
            AdminSocketAlert.sendMessageAlert("chatevent", "BauCuaLoop", "12121212121212" );
            i++;
        }
    }
}
