package game.modules.lobby;

import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OTPCheck {
    public boolean checkUserActiveOTP(String nickname) throws SQLException {
        boolean check_null = false;
        boolean check = false;
        UserOTP uotp = null;
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        String sql = "SELECT * FROM active WHERE nickname=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();

        while (rs.next()){
            uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"),rs.getInt("active"), rs.getLong("creat_time"), rs.getLong("active_time"), rs.getInt("turn"));
        }

        if (uotp == null) {
            check_null = false;
        }else {
            check_null = true;
        }

        if(check_null == true){
            if(uotp.getActive() == 1){
                check = true;
            }else{
                check = false;
            }
        }else {
            check = false;
        }

        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return check;
    }
}
