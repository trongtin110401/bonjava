package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.minigame.TaiXiuChatMsg;

import javax.servlet.http.HttpServletRequest;

public class TaiXiuAdminKubetListChatProcessor implements BaseProcessor<HttpServletRequest, String> {
  @Override
  public String execute(Param<HttpServletRequest> param) {
    String res = "0";
    HttpServletRequest request = (HttpServletRequest) param.get();
    String content = request.getParameter("content");
    if (content == null) {
      return res;
    }
    try {
      TaiXiuChatMsg obj = new TaiXiuChatMsg(null , content);
      obj.setStatus(0);
      CacheService cacheService = new CacheServiceImpl();
      cacheService.setObject("admin_lst_msg_kubet", obj);
      return "1";
    } catch (Exception e) {
      return res;
    }
  }
}
