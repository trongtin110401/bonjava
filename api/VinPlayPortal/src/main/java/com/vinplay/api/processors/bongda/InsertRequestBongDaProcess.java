package com.vinplay.api.processors.bongda;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.api.processors.bongda.response.InsertRequestResponse;
import com.vinplay.bongda.dao.KeoBongDaDao;
import com.vinplay.bongda.dao.UserBetBongDaDao;
import com.vinplay.bongda.dao.impl.KeoBongDaImpl;
import com.vinplay.bongda.dao.impl.UserBetBongDaimpl;
import com.vinplay.bongda.entities.KeoBongDa;
import com.vinplay.bongda.entities.UserBetBongDa;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.usercore.utils.GameCommon;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.Consts;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.VinPlayUtils;
import org.apache.log4j.Logger;
import org.python.parser.ast.Str;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

public class InsertRequestBongDaProcess implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");

    @Override
    public String execute(Param<HttpServletRequest> param) {
        HttpServletRequest request = param.get();
        UserBetBongDaDao dao = new UserBetBongDaimpl();
        KeoBongDaDao keoBongDaDao = new KeoBongDaImpl();
        UserService userService = new UserServiceImpl();
        InsertRequestResponse response = new InsertRequestResponse();
        try {
            String accessToken = request.getParameter("token");
            String ID = String.valueOf(VinPlayUtils.generateTransId());
            String session = request.getParameter("session");
            if (!validateSession(session)) {
                response.setCode("0");
                return response.toJson();
            }
            String idTran = request.getParameter("idTran");

            String nickname = request.getParameter("nickname");
            if (accessToken == null || accessToken.isEmpty() || nickname == null || nickname.isEmpty()) {
                response.setCode("0");
                return response.toJson();
            }

            if (!userService.checkAccesstoken(nickname, accessToken)) {
                response.setCode("0");
                return response.toJson();
            }
            long moneyBet = Long.parseLong(request.getParameter("moneyBet"));

            int result = 0; // kết quả , 0 là chưa 1 là win ăn tiền phải trả
            long moneyWin = 0;
            int betType = Integer.parseInt(request.getParameter("betType"));
            // 1-2 , 1: Doi A , 2  doi B chẵn đội B , lẻ đội A
            double chapTrai = this.getChapTrai(betType, keoBongDaDao, idTran, session); // mac dinh la 0
            double tiLeAn = this.getTileAnTrai(betType, keoBongDaDao, idTran, session); //
            String CreatedAt = VinPlayUtils.getCurrentDateTime();
            String UpdatedAt = VinPlayUtils.getCurrentDateTime();
            if (chapTrai < 0) {
                if (chapTrai == -2) {
                    response.setCode("4");
                } else {
                    response.setCode("0");
                }

                return response.toJson();
            }
            if (tiLeAn < 0) {
                response.setCode("0");
                return response.toJson();
            }
            if (moneyBet <= 0) {
                response.setCode("0");
                return response.toJson();
            }
            if (betType < 1) {
                response.setCode("0");
                return response.toJson();
            }

            double feeWithdraw = GameCommon.getValueDouble("RATIO_CASHOUT_BANK");
            int totalAmount = (int) (moneyBet * feeWithdraw);
            int amount = -totalAmount;
            int totalFee = (int) (totalAmount - moneyBet);

            long moneyCurrent = userService.getCurrentMoneyUserCache(nickname, "vin");
            if (moneyCurrent < moneyBet) {
                response.setCode("2");
                return response.toJson();
            } // không đủ tiền mà đòi bet

            HazelcastInstance client = HazelcastClientFactory.getInstance();
            if (client == null) {
                response.setCode("0");
                return response.toJson();
            }
            IMap<String, UserModel> userMap = client.getMap("users");

            if (!userMap.containsKey((Object) nickname)) {
                response.setCode("0");
                return response.toJson();
            }
            MoneyResponse moneyRes = userService.updateMoney(nickname, amount, "vin", "Bóng Đá", "Đặt cược bóng đá", "Đặt bóng đá", totalFee, null, TransType.NO_VIPPOINT);
            if (!moneyRes.isSuccess()) {
                response.setCode("0");
                return response.toJson();
            }
            UserBetBongDa userBetBongDa = new UserBetBongDa(ID, session, idTran, nickname, moneyBet, result, moneyWin, betType, chapTrai, tiLeAn, CreatedAt, UpdatedAt);
            dao.insertUserBetBongDa(userBetBongDa);
            BroadCastUserMoney.pushBroadCast(nickname);
            long myCrnt = userService.getCurrentMoneyUserCache(nickname, "vin");
            response.setCode("1");
            response.setMoney(myCrnt);
            return response.toJson(); // bet thanh công
        } catch (Exception e) {
            return "0";
        }


    }

    private double getChapTrai(int type, KeoBongDaDao keoBongDaDao, String idTran, String session) {
        KeoBongDa keoBongDa = keoBongDaDao.findKeoBongDaBySessionAndId(session, idTran);
        if (keoBongDa.getStatus() > 3) return -2; // 3 là đóng cược
        switch (type) {
            case 1:
                return Double.parseDouble(keoBongDa.tiLeDoiAChapCaTran);
            case 2:
                return Double.parseDouble(keoBongDa.tiLeDoiBChapCaTran);
            case 3:
                return Double.parseDouble(keoBongDa.tileDoiAChapTaiXiu);
            case 4:
                return Double.parseDouble(keoBongDa.tileDoiBChapTaiXiu);
            case 5:
                return Double.parseDouble(keoBongDa.tileDoiAChapHiep1);
            case 6:
                return Double.parseDouble(keoBongDa.tileDoiBChapHiep1);
            case 7:
                return Double.parseDouble(keoBongDa.tileDoiAChapHiep2);
            case 8:
                return Double.parseDouble(keoBongDa.tileDoiBChapHiep2);
        }
        return -1;
    }

    private double getTileAnTrai(int type, KeoBongDaDao keoBongDaDao, String idTran, String session) {
        KeoBongDa keoBongDa = keoBongDaDao.findKeoBongDaBySessionAndId(session, idTran);
        if (keoBongDa.getStatus() > 3) return -1; // 3 là đóng cược
        switch (type) {
            case 1:
                return keoBongDa.tileAnDoiACaTran;
            case 2:
                return keoBongDa.tileAnDoiBCaTran;
            case 3:
                return keoBongDa.tileAnDoiATaiXiu;
            case 4:
                return keoBongDa.tileAnDoiBTaiXiu;
            case 5:
                return keoBongDa.tileAnDoiAHiep1;
            case 6:
                return keoBongDa.tileAnDoiBHiep1;
            case 7:
                return keoBongDa.tileAnDoiAHiep2;
            case 8:
                return keoBongDa.tileAnDoiBHiep2;
        }
        return -1;
    }

    private boolean validateSession(String session) {
        String currentSession = String.valueOf(VinPlayUtils.getSessionDate());
        currentSession = currentSession.replace("-", "");
        if (currentSession.equals(session)) return true;
        return false;
    }
}
