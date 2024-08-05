package com.vinplay.api.backend.processors.slot;

import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.MiniPokerService;
import com.vinplay.dal.service.PokeGoService;
import com.vinplay.dal.service.SlotMachineService;
import com.vinplay.dal.service.impl.MiniPokerServiceImpl;
import com.vinplay.dal.service.impl.PokeGoServiceImpl;
import com.vinplay.dal.service.impl.SlotMachineServiceImpl;
import com.vinplay.usercore.service.impl.CacheServiceImpl;
import com.vinplay.utils.SlotNohuObject;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.exceptions.KeyNotFoundException;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Random;

public class SetSlotJackpot implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "backend");

    protected SlotMachineService slotService = new SlotMachineServiceImpl();
    private PokeGoService pgService = new PokeGoServiceImpl();
    private MiniPokerService mpService = new MiniPokerServiceImpl();
    private Random r = new Random(System.currentTimeMillis());
    private static final String CACHE_JACK_POT_VALUE = "pot_value_jackpot";

    private static final String TYPE_USER = "fake";

    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        try {
            String nickname = request.getParameter("nickname");
            String action = request.getParameter("act");
            String gameName = request.getParameter("game");
            String betValue = request.getParameter("betValue");
            String totalPrize = request.getParameter("totalPrize");
            String typeUser = request.getParameter("typeUser");
            String currentTimeStr = DateTimeUtils.getCurrentTime();
            // tạm fake số tiền hũ nhận được
            if (nickname == null || nickname.isEmpty() || action == null || action.isEmpty() || gameName == null || gameName.isEmpty() || typeUser.isEmpty()) {
                return "";
            }
            if (Objects.equals(typeUser.toLowerCase(), TYPE_USER)) {
                long[] arr = {(long) 576000, 1034567, 4034567};
                int rnd = new Random().nextInt(arr.length);
                long prize = totalPrize.isEmpty() ? this.getMoneyPot(betValue, gameName) : Long.parseLong(totalPrize);
                int betMoney = betValue.isEmpty() ? 100 : Integer.parseInt(betValue);
                if (Objects.equals(gameName.toUpperCase(), Games.CANDY.getName().toUpperCase())) {
                    this.pgService.addTop(nickname, betMoney, prize, 1, currentTimeStr, 3);
                } else if (Objects.equals(gameName.toUpperCase(), Games.MINI_POKER.getName().toUpperCase())) {
                    this.mpService.logMiniPoker(nickname, betMoney, (short) 1, prize, "builder.toString()", 12, 12, 1);
                } else {
                    this.slotService.logNoHu(this.r.nextInt(500), gameName, nickname, betMoney, "linesStr", "matrixStr", "builderLinesWin.toString()",
                            "builderPrizesOnLine.toString()", prize, (short) 3, currentTimeStr);
                }
                reset(gameName, betMoney);
                SlotNohuObject msg = new SlotNohuObject(nickname, (byte) 1, prize, gameName);
                CacheService cacheService = new com.vinplay.dal.service.impl.CacheServiceImpl();
                cacheService.setObject("notifyNohu", msg);
                if (action.equals("add")) {
                    response.setSuccess(true);
                    response.setErrorCode("0");
                    return response.toJson();
                }
            } else {
                if (action.equals("add")) {
                    return addUserToJackpotGame(nickname, gameName, betValue).toJson();
                }
                if (action.equals("remove")) {
                    return RemoveUserToJackpotGame(gameName).toJson();
                }
            }


        } catch (Exception e) {
            logger.debug((Object) e);
        }
        return response.toJson();
    }

    Long getMoneyPot(String betValue, String gameName) {
        CacheServiceImpl cacheService = new CacheServiceImpl();
        try {
            return Long.parseLong(cacheService.getValueStr(CACHE_JACK_POT_VALUE + "_" + betValue + "_" + gameName));
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    void reset(String gameName, int type) {
        try {
            CacheServiceImpl cacheService = new CacheServiceImpl();
            String cacheKey;
            switch (type) {
                case 100:
                    cacheKey = "reset_pot_" + gameName + "_100";
                    cacheService.setValue(cacheKey, 1);
                    break;
                case 1000:
                    cacheKey = "reset_pot_" + gameName + "_1000";
                    cacheService.setValue(cacheKey, 1);
                    break;
                case 10000:
                    cacheKey = "reset_pot_" + gameName + "_10000";
                    cacheService.setValue(cacheKey, 1);
                    break;
                case 5000:
                    cacheKey = "reset_pot_" + gameName + "_5000";
                    cacheService.setValue(cacheKey, 1);
                    break;
            }
        } catch (Exception e) {
            logger.error(" Reset JackPot " + gameName + " error with " + e.getMessage());
        }
    }


    BaseResponseModel addUserToJackpotGame(String nickname, String gameName, String betValue) {
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        String cacheKey = "user_force_jackpot_" + gameName;
        String cacheKeyBetValue = "bet_value_jackpot_" + gameName;
        try {
            CacheServiceImpl cacheService = new CacheServiceImpl();
//            String userForce = "";
//            try{
//                userForce = cacheService.getValueStr(cacheKey);
//            }catch (Exception e){
//
//            }
//            if(!userForce.isEmpty()){
//                response.setErrorCode("1002");
//                response.setSuccess(false);
//                return response;
//            }
            cacheService.setValue(cacheKey, nickname);
            cacheService.setValue(cacheKeyBetValue, betValue);
            response.setSuccess(true);
            response.setErrorCode("0");
            return response;
        } catch (Exception e) {

            return response;
        }
    }

    BaseResponseModel RemoveUserToJackpotGame(String gameName) {
        BaseResponseModel response = new BaseResponseModel(false, "1001");
        String cacheKey = "user_force_jackpot_" + gameName;
        try {
            CacheServiceImpl cacheService = new CacheServiceImpl();

            cacheService.removeKey(cacheKey);
            response.setSuccess(true);
            response.setErrorCode("0");
            return response;
        } catch (Exception e) {

            return response;
        }
    }
}
