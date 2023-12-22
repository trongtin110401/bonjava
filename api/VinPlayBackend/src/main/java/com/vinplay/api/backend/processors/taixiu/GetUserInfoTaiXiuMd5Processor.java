package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.miniGame.TaiXiuAdminReportObj;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.minigame.TaiXiuAdminReportResponse;

import javax.servlet.http.HttpServletRequest;

//todo: xóa khi okee ws
public class GetUserInfoTaiXiuMd5Processor implements BaseProcessor<HttpServletRequest, String> {
  @Override
  public String execute(Param<HttpServletRequest> param) {
    String res = "0";
    try {
      CacheService cacheService = new CacheServiceImpl();
      TaiXiuAdminReportObj taiXiuAdminReportObj = (TaiXiuAdminReportObj) cacheService.getObject("user_tai_xiu_md5");
      TaiXiuAdminReportResponse response = new TaiXiuAdminReportResponse(true, "0");
      response.setMoneyTai(taiXiuAdminReportObj.getMoneyTai());
      response.setMoneyXiu(taiXiuAdminReportObj.getMoneyXiu());
      response.setNguoiChoiBetTai(taiXiuAdminReportObj.getNumberUserRealTai());
      response.setNguoiChoiBetXiu(taiXiuAdminReportObj.getNumberUserRealXiu());
      response.setMoneyTaiFull(taiXiuAdminReportObj.getMoneyTaiFull());
      response.setMoneyXiuFull(taiXiuAdminReportObj.getMoneyXiuFull());
      response.setPhienId(taiXiuAdminReportObj.getPhienId());
//      response.setContributors(taiXiuAdminReportObj.getContributors());
      response.setNumberUserAndBotBetTai(taiXiuAdminReportObj.getNumberUserAndBotBetTai());
      response.setNumberUserAndBotBetXiu(taiXiuAdminReportObj.getNumberUserAndBotBetXiu());
      response.setRealTime(taiXiuAdminReportObj.getRealTime());
      response.setBettingRound(taiXiuAdminReportObj.isBettingRound());
//      if (taiXiuAdminReportObj.getLstMsg().size() > 10)
//        taiXiuAdminReportObj.getLstMsg().subList(0, taiXiuAdminReportObj.getLstMsg().size() - 10).clear();
//      response.setLstMsg(taiXiuAdminReportObj.getLstMsg());
      return response.toJson();
    } catch (Exception e) {
      return res;
    }
  }
}
