package game.models.baucuato2;

import java.io.Serializable;

public class SetBauCuaKetqua implements Serializable {
    String status;
    byte[] listDices;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public byte[] getListDices() {
        return listDices;
    }

    public void setListDices(byte[] listDices) {
        this.listDices = listDices;
    }

    public SetBauCuaKetqua(String status, byte[] listDices) {
        this.status = status;
        this.listDices = listDices;
    }

}
