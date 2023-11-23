package com.vinplay.api.processors.secretcode;

import com.vinplay.secretcode.service.impl.UserSecretServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;

public class CheckUpdateSecretCodeProcessor implements BaseProcessor<HttpServletRequest, String>

    {
        private static final Logger logger = Logger.getLogger((String)"api");

        @Override
        public String execute(Param<HttpServletRequest> param) {
            HttpServletRequest request = (HttpServletRequest)param.get();
            String username = request.getParameter("u");
            UserSecretServiceImpl userSecretService = new UserSecretServiceImpl();
            int isNotify = 0;
            String mess ="";
            String mess2 ="";
            try {
                JSONObject response = new JSONObject(GameCommon.getValueStr("game_notify"));
                isNotify = Integer.parseInt(response.getString("isnotify"));
                mess = response.getString("mess");
                mess2 = response.getString("mess2");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (KeyNotFoundException e) {
                e.printStackTrace();
            }
            if(userSecretService.findCode(username)!=null){
                return "{\"ok\":0,\"notify\":"+isNotify+",\"text\":\""+mess+"\",\"text2\":\""+mess2+"\"}";
            } else {
                return "{\"ok\":0,\"notify\":"+isNotify+",\"text\":\""+mess+"\",\"text2\":\""+mess2+"\"}";
            }

        }
    }
