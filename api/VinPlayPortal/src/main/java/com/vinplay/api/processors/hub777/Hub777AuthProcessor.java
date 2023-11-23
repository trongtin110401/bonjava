package com.vinplay.api.processors.hub777;

import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class Hub777AuthProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "mydebug");

    @Override
    public String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        HttpServletRequest request = (HttpServletRequest) param.get();

        String data = request.getParameter("KMData");
        String Method = request.getParameter("methodId");
//        logger.error("aaa" + data);
//        System.out.println(new String(decrypt(data.getBytes(),ENCRYPTIONKEY.getBytes())));
//
//        String rawData = new String(decrypt(data.getBytes(),ENCRYPTIONKEY.getBytes()));
//        Gson gson = new Gson();
//        System.out.println(gson.toJson(dataStringToObject(rawData)));

        //userService.getU
        try {
//            String accessToken = AESEncryption.getTokenFromRawData(data);
            Map<String, String> mapToken = AESEncryption.dataStringToObject(data);
            String accessToken = mapToken.get("Token");
            if (accessToken == null || accessToken.isEmpty()) {
                accessToken = mapToken.get("UserName");
            }
            logger.error("method day " + Method + " access day" + accessToken);
            String nickname = "";
            nickname = this.getUserNameByAccessToken(accessToken);
            if (userService.checkAccesstoken(nickname, accessToken)) {
                if (Method.equals("1")) {
                    //lay thong tin
                    long currentMoney = userService.getCurrentMoneyUserCache(nickname, "vin");
                    UserModel userModel = userService.getUserByNickName(nickname);
//                    logger.error("gui lay thong tin " + "{ \"ErrorCode\": 0, \"Result\": true, \"Message\": \"\", \"Data\": \"{\\\"AgentId\\\":1,\\\"UserID\\\":" + userModel.getId() + ",\\\"Nickname\\\":\\\"" + userModel.getNickname() + "\\\",\\\"AccountBalance\\\":" + currentMoney + "}\" }");
                    return "{ \"ErrorCode\": 0, \"Result\": true, \"Message\": \"\", \"Data\": \"{\\\"AgentId\\\":1,\\\"UserID\\\":" + userModel.getId() + ",\\\"Nickname\\\":\\\"" + userModel.getNickname() + "\\\",\\\"AccountBalance\\\":" + currentMoney + "}\" }";
                } else if (Method.equals("2")) {
                    //tru tien
                    String TotalBet = mapToken.get("TotalBet");
                    String GameName = mapToken.get("GameName");
                    long moneyBet = Math.abs(Long.parseLong(TotalBet));
                    UserModel userModel = userService.getUserByNickName(nickname);
                    long firstCoin = userModel.getVin();
                    String TxId = mapToken.get("MatchID");
                    MoneyResponse moneyRes = userService.updateMoney(nickname, -moneyBet, "vin", GameName, "Quay " + GameName, "Quay " + GameName, 0L, Long.valueOf(TxId), TransType.END_TRANS);
                    if (moneyRes != null && moneyRes.isSuccess()) {
                        long lasmoney = moneyRes.getCurrentMoney();
                        String resultData = "TxId=" + TxId + "&UserName=" + nickname + "&Currency=" + firstCoin + "&Coin=" + lasmoney + "&RTPType=2";

                        String encodeData = AESEncryption.encryptData(resultData);
//                        logger.error("Send bet " + TotalBet + " ket qua:" + resultData + " server khi encript: " + encodeData);
                        return "{\"ErrorCode\":0,\"Result\":true,\"Message\":\"\",\"Data\":\"" + encodeData + "\"}";
                    } else {
                        return "{\"ErrorCode\":1002,\"Result\":fale,\"Message\":\"\",\"Data\":\"" + data + "\"}";
                    }
//                    ("TxId={0}&Token={1}&Currency={2}&TotalBet={3}&TimeStamp={4}&Ip={5}&GameName={6}&Platform={7}&GameId={8}&BetDetails={9}&MatchID={10})
                } else if (Method.equals("3")) {
//                    ("TxId={0}&UserName={1}&Currency={2}&WinCoin={3}&TimeStamp={410}&Ip={5}&GameName={6}&Platform={7}&GameId={8}&IsFreeTurn={9}&MatchID={10}&TotalBet={11}",
                    String WinCoin = mapToken.get("WinCoin");
                    String GameName = mapToken.get("GameName");
                    long moneyBet = Math.abs(Long.parseLong(WinCoin));

                    UserModel userModel = userService.getUserByNickName(nickname);
                    long firstCoin = userModel.getVin();
                    String TxId = mapToken.get("MatchID");
                    if (moneyBet <= 0) {
                        String resultData = "TxId=" + TxId + "&UserName=" + nickname + "&Currency=" + firstCoin + "&Coin=" + firstCoin;

                        String encodeData = AESEncryption.encryptData(resultData);
//                        logger.error("Send win " + WinCoin + " ket qua:" + resultData + " server khi encript: " + encodeData);
                        return "{\"ErrorCode\":0,\"Result\":true,\"Message\":\"\",\"Data\":\"" + encodeData + "\"}";
                    }
                    MoneyResponse moneyRes = userService.updateMoney(nickname, moneyBet, "vin", GameName, "Thắng " + GameName, "Thắng " + GameName, 0L, Long.valueOf(TxId), TransType.START_TRANS);
                    if (moneyRes != null && moneyRes.isSuccess()) {
                        long lasmoney = moneyRes.getCurrentMoney();
                        String resultData = "TxId=" + TxId + "&UserName=" + nickname + "&Currency=" + firstCoin + "&Coin=" + lasmoney;

                        String encodeData = AESEncryption.encryptData(resultData);
//                        logger.error("Send win " + WinCoin + " ket qua:" + resultData + " server khi encript: " + encodeData);
                        return "{\"ErrorCode\":0,\"Result\":true,\"Message\":\"\",\"Data\":\"" + encodeData + "\"}";
                    } else {
                        return "{\"ErrorCode\":1002,\"Result\":fale,\"Message\":\"\",\"Data\":\"" + data + "\"}";
                    }
                } else if (Method.equals("6")) {
                    String GameId = request.getParameter("GameId");
                    BroadCastUserMoney.pushBroadOutGame(nickname,GameId);
                    return "{\"ErrorCode\":0,\"Result\":true,\"Message\":\"\"}";
                } else if(Method.equals("7")) {
                    //tru tien
                    String TotalBet = mapToken.get("TotalBet");
                    String GameName = mapToken.get("GameName");
                    long moneyBet = Math.abs(Long.parseLong(TotalBet));
                    UserModel userModel = userService.getUserByNickName(nickname);
                    long firstCoin = userModel.getVin();
                    String TxId = mapToken.get("MatchID");
                    MoneyResponse moneyRes = userService.updateMoney(nickname, -moneyBet, "vin", GameName, "Quay " + GameName, "Quay " + GameName, 0L, Long.valueOf(TxId), TransType.END_TRANS);
                    if (moneyRes != null && moneyRes.isSuccess()) {
                        long lasmoney = moneyRes.getCurrentMoney();
                        String resultData = "TxId=" + TxId + "&UserName=" + nickname + "&Currency=" + firstCoin + "&Coin=" + lasmoney + "&RTPType=2";

                        String encodeData = AESEncryption.encryptData(resultData);
//                        logger.error("Send bet " + TotalBet + " ket qua:" + resultData + " server khi encript: " + encodeData);
                        return "{\"ErrorCode\":0,\"Result\":true,\"Message\":\"\",\"Data\":\"" + encodeData + "\"}";
                    } else {
                        return "{\"ErrorCode\":1002,\"Result\":fale,\"Message\":\"\",\"Data\":\"" + data + "\"}";
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return "{ \"ErrorCode\": 1, \"Result\": true, \"Message\": \"\", \"Data\": \"{\\\"AgentId\\\":1,\\\"UserID\\\":1,\\\"Nickname\\\":\\\"Nickname_Agent1\\\",\\\"AccountBalance\\\":0}\" }";
    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

}
