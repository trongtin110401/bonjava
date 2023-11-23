package game.modules.lobby;

import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class UserDAO {
    public long GetVin(String nickname) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");

        long vin = 0;
        String sql = "SELECT `vin` FROM `vinplay`.`users` WHERE `nick_name`=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            vin = rs.getLong(1);
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return  vin;
    }
}
