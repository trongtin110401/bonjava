package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.miniGame.TaiXiuSetAmountBotFake;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;

public class SetBotFakeTaiXiuKubetProcessor implements BaseProcessor<HttpServletRequest, String> {

  private static final Logger logger = Logger.getLogger((String)"backend");

  @Override
  public String execute(Param<HttpServletRequest> param) {
    String res = "0";
    HttpServletRequest request = param.get();
    String numberBotTaiFake = request.getParameter("numberBotTaiFake").trim();
    String numberBotXiuFake = request.getParameter("numberBotXiuFake").trim();
    try {
      CacheService cacheService = new CacheServiceImpl();
      TaiXiuSetAmountBotFake taiXiuSetAmountBotFake = new TaiXiuSetAmountBotFake(true, "0");
      taiXiuSetAmountBotFake.setNumberBotTaiFake(Integer.parseInt(numberBotTaiFake));
      taiXiuSetAmountBotFake.setNumberBotXiuFake(Integer.parseInt(numberBotXiuFake));
      cacheService.setObject("taixiu_bot_fake_amount_kubet", taiXiuSetAmountBotFake);
      return "1";
    } catch (Exception e) {
      logger.error("SetBotFakeTaiXiuProcessor error with :" + e);
      return res;
    }
  }
}
