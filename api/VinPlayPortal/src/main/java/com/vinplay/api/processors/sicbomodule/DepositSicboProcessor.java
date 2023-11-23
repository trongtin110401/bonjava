package com.vinplay.api.processors.sicbomodule;

import com.vinplay.api.utils.PortalUtils;
import com.vinplay.common.HttpCommon;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dal.service.impl.AceMoneyService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;
import okhttp3.*;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DepositSicboProcessor implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger(DepositSicboProcessor.class);
    public static final String brand = "b52";
    public static final String privatekey = "b52funvn";
    public static final String host = "https://isicbo.bandoluuniem.net";

    public synchronized String execute(Param<HttpServletRequest> param) {
        UserService userService = new UserServiceImpl();
        BaseResponseModel aceResponse = new BaseResponseModel(false, "500");
        HttpServletRequest request = (HttpServletRequest) param.get();
        String nickname = request.getParameter("nickname");
        String accessToken = request.getParameter("at");
        int type = Integer.parseInt(request.getParameter("type"));
        Long mMoney = 0l;
//        return "{\"code\":0,\"message\":null,\"currentBalanceGame\":0.0,\"data\":\"\"}";
        synchronized (this) {
            if (userService.checkAccesstoken(nickname, accessToken)) {
                long currentMoney = userService.getCurrentMoneyUserCache(nickname, "vin");
                mMoney = currentMoney;
                if (nickname.isEmpty()) return aceResponse.toJson();
                if (!PortalUtils.allowDepositAce(nickname)) {
                    aceResponse.setErrorCode("nạp rút qua nhanh");
                    return aceResponse.toJson();
                }
                try {
                    if (type == 1) { //cong tien
                        aceResponse = userService.updateMoneyFromAdmin(nickname, (long) -mMoney, "vin",
                                "Sicbo", "Chuyển tiền vào sicbo",
                                "Chuyển tiền vào sicbo", 0);

                        if (aceResponse.isSuccess()) { // call sang ace , nếu thất bại thì cộng lại tiền
                            String myres = this.changeBalance(nickname, mMoney + "", 1);
                        }
                    } else if (type == 2) {
                        String money = getBalance(nickname);
                        if (money != null) {
                            mMoney = Long.parseLong(money);
                            if (mMoney <= 0) {
                                return aceResponse.toJson();
                            }
                        } else {
                            return aceResponse.toJson();
                        }
                        String myres = this.changeBalance(nickname, mMoney + "", 2);
                        if (myres != null) {
                            aceResponse = userService.updateMoneyFromAdmin(nickname, (long) mMoney, "vin",
                                    "Sicbo", "Nhận tiền từ SicBo",
                                    "Nhận tiền từ SicBo", 0);
                        } else {
                            aceResponse.setErrorCode(" chuyển tiền sang game thất bại lý do: " + myres);
                        }
                    }
                    BroadCastUserMoney.pushBroadCast(nickname);

                } catch (Exception e) {
                    e.printStackTrace();
                    aceResponse.setErrorCode("chuyển tiền thất bại " + e.getMessage());
                }

            }
        }

        //    userService.getU

        AceMoneyService aceMoneyService = new AceMoneyService(); // khóa mõm giao dịch nạp rút trong vòng
        aceMoneyService.banDepositUser(nickname, 10000);
        return aceResponse.toJson();
    }

    private String changeBalance(String username, String amount, int type) {
        try {
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(host + "/changeBalance?username=" + username + "&brand=" + brand + "&privatekey=" + privatekey + "&amount=" + amount + "&type=" + type)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            if (data.contains("{\"status\":0")) {
                return data;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getBalance(String username) throws IOException {
        OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(host + "/getAccess?username=" + username + "&brand=" + brand + "&nickName=" + username + "&privatekey=" + privatekey)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        String data = response.body().string();
        if (data.contains("{\"status\":0")) {
            String money = bocTachRegex(data, "(\"money\":)(\\d+)(})", 2);
            return money;
        }
        return null;
    }

    public static String bocTachRegex(String raw, String regex, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(raw);
        if (matcher.find()) {
            String output = matcher.group(group);
            System.out.println(output);
            return output;
        }
        return null;
    }
}
