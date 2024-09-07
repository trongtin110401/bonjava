package com.vinplay.api.processors.acemodule;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.pools.ConnectionPool;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class GetConfigureHotUpdate implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger("api");

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = (HttpServletRequest) param.get();
        String t = request.getParameter("t");
        String k = request.getParameter("k");
        logger.error(t + " | " + k);

        if (k.equalsIgnoreCase("1asdfasdfasdf2ef2efsfd")) {
            Connection conn = null;
            PreparedStatement stm = null;
            ResultSet rs = null;
            try {
                conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
                String sql = "SELECT * FROM game_config WHERE id=?";
                stm = conn.prepareStatement(sql);
                stm.setInt(1, Integer.parseInt(t));
                rs = stm.executeQuery();
                if (rs.next()) {
                    String code = rs.getString("value");
                    logger.error("có code" + code);
                    return code;
                }
                logger.error("khong co");
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            } finally {
                closeConnection(stm, rs, conn);
            }
        }

        return "{\"code\":1,\"description\":\"Da co loi xay ra\"}";
    }

    private void closeConnection( Statement statement, ResultSet resultSet, Connection connection) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
