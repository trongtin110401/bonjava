package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;

public class SetResultTaiXiuMd5Processor implements BaseProcessor<HttpServletRequest, String> {
  @Override
  public String execute(Param<HttpServletRequest> param) {
    String res = "0";
    HttpServletRequest request = (HttpServletRequest) param.get();
    String rs = request.getParameter("action");
    String nohu = request.getParameter("nohu");
    if (rs == null || nohu == null) {
      return res;
    }
    CacheService cacheService = new CacheServiceImpl();
    try {
      cacheService.setValue("tai_xiu_be_cang_md5", rs);
      cacheService.setValue("tai_xiu_no_hu_md5", nohu);
      return "1";
    } catch (Exception e) {
      return res;
    }
  }
}
