package game.modules.lobby;

import com.vinplay.vbee.common.pools.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class TanThuDAO {
    public ArrayList<CodeTT> GetCode() throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");

       ArrayList<CodeTT> listcode = new ArrayList<>();
        String sql = "SELECT * FROM vinplay.giftcodett";
        PreparedStatement stm = conn.prepareStatement(sql);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
           CodeTT tt = new CodeTT(rs.getString("code"), rs.getInt("money"), rs.getString("timelog"), rs.getInt("stop"));
           listcode.add(tt);
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return  listcode;
    }

    public boolean CheckUseCode(String nickname) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        UseCode tt = null;
        boolean check = false;

        String sql = "SELECT * FROM vinplay.codetanthu WHERE nickname=? AND `use`=1";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            tt = new UseCode(rs.getString("nickname"), rs.getString("code"), rs.getInt("use"), rs.getString("timelog"), rs.getString("username"), rs.getString("phone"), rs.getInt("active"));
        }
        if(tt == null){
            check = false;
        }else{
            check = true;
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return  check;
    }

    public void InsertCode(UseCode usercode) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "INSERT INTO vinplay.codetanthu (`nickname`,`CODE`, `use`, `timelog`, `username`, `phone`, `active`) VALUES(?,?,?,?,?,?,?)";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, usercode.getNickname());
            stm.setString(2,usercode.getCode());
            stm.setInt(3, usercode.getUse());
            stm.setString(4, usercode.getTimelog());
            stm.setString(5, usercode.getUsername());
            stm.setString(6, usercode.getPhone());
            stm.setInt(7, usercode.getActive());

            int rs = stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        }catch (Exception e) {
            throw e;
        }
    }

    public UserOTP GetActive(String nickname) throws Exception{
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        UserOTP uotp = null;
        String sql = "SELECT * FROM vinplay.active WHERE nickname=?";
        PreparedStatement stm = conn.prepareStatement(sql);
        stm.setString(1, nickname);
        ResultSet rs = stm.executeQuery();
        while (rs.next()) {
            uotp = new UserOTP(rs.getString("nickname"), rs.getString("username"), rs.getString("phone"), rs.getString("otp"),rs.getInt("active"), rs.getLong("creat_time"), rs.getLong("active_time"), rs.getInt("turn"));
        }
        rs.close();
        stm.close();
        if (conn != null) {
            conn.close();
        }
        return  uotp;
    }



}
