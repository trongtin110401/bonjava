package com.vinplay.api.processors;

import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;

public class KubetStreamUrlProcessor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) throws SQLException {
        HttpServletRequest request = param.get();
        String game = request.getParameter("game");
        if (game.equals("taixiu")) {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet("https://newjson.thietkewebcobac.com/?ID=taixiua");
            try {
                HttpResponse httpResponse = httpClient.execute(httpget);
                return EntityUtils.toString(httpResponse.getEntity());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpget = new HttpGet("https://newjson.thietkewebcobac.com/?ID=xocdiaa");
            try {
                HttpResponse httpResponse = httpClient.execute(httpget);
                return EntityUtils.toString(httpResponse.getEntity());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
