package game.entities;

import bitzero.server.entities.User;

import java.util.ArrayList;

public class UserOnePayTransaction {
    String transId;
    User user;
    ArrayList<String> listTrans;

    public ArrayList<String> getListTrans() {
        return listTrans;
    }

    public void setListTrans(ArrayList<String> listTrans) {
        this.listTrans = listTrans;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserOnePayTransaction( User user,String transId) {
        this.transId = transId;
        this.user = user;
    }

    public UserOnePayTransaction() {
    }

    @Override
    public String toString() {
        return "UserOnePayTransaction{" +
                "transId='" + transId + '\'' +
                ", user=" + user +
                '}';
    }
}
