package com.vinplay.api.processors.acemodule;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.pools.ConnectionPool;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class GetConfigureGame implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");
    private static HashMap<String,String> dataCode = new HashMap<>();
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String t = request.getParameter("t");
        if(t.equalsIgnoreCase("100")) {
            dataCode = new HashMap<>();
            return "OK";
        }
        if(dataCode.containsKey(t)) {
            return "{\"code\":0,\"description\":"+dataCode.get(t)+"}";
        }
        try {
            Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
            if (t.equalsIgnoreCase("49") || t.equalsIgnoreCase("54")) {
                String sql = "SELECT * FROM game_config WHERE id=?";
                PreparedStatement stm = conn.prepareStatement(sql);
                stm.setInt(1, Integer.parseInt(t));
                ResultSet rs = stm.executeQuery();
                if (rs.next()) {
                    String code = rs.getString("value");
                    dataCode.put(t,code);
                    return "{\"code\":0,\"description\":"+code+"}";

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "{\"code\":1,\"description\":\"Da co loi xay ra\"}";
    }
}
