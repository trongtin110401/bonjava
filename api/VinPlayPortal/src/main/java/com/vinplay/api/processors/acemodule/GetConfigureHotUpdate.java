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

public class GetConfigureHotUpdate implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String t = request.getParameter("t");
        String k = request.getParameter("k");
        logger.error(t+" | " + k);
        if (k.equalsIgnoreCase("1asdfasdfasdf2ef2efsfd")) {
            try {
                logger.error("conecct mysql");
                Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
                String sql = "SELECT * FROM game_config WHERE id=?";
                PreparedStatement stm = conn.prepareStatement(sql);
                stm.setInt(1, Integer.parseInt(t));
                ResultSet rs = stm.executeQuery();
                if (rs.next()) {
                    String code = rs.getString("value");
                    logger.error("có code"+ code);
                    return code;
                }
                logger.error("khong co");
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return "{\"code\":1,\"description\":\"Da co loi xay ra\"}";
    }
}
