package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.miniGame.TaiXiuAdminReportObj;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.minigame.TaiXiuAdminReportResponse;

import javax.servlet.http.HttpServletRequest;

public class GetListBotTaiXiuProcessor implements BaseProcessor<HttpServletRequest, String> {
  @Override
  public String execute(Param<HttpServletRequest> param) {
    String res = "0";
    try {
      CacheService cacheService = new CacheServiceImpl();
      TaiXiuAdminReportObj taiXiuAdminReportObj = (TaiXiuAdminReportObj) cacheService.getObject("user_tai_xiu");
      TaiXiuAdminReportResponse response = new TaiXiuAdminReportResponse(true, "0");
      response.setGetListChatUsers(taiXiuAdminReportObj.getGetListChatUsers());
      return response.toJson();
    } catch (Exception e) {
      return res;
    }
  }
}
