package com.vinplay.api.processors.rutbankapi;

import bitzero.server.extensions.data.BaseMsg;
import com.vinplay.api.processors.CheckBank.KiemTra;
import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dichvuthe.dao.CashoutDao;
import com.vinplay.dichvuthe.dao.impl.CashoutDaoImpl;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.service.HistoryTransService;
import com.vinplay.lognaprut.service.impl.HistoryTransServiceImpl;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.rmq.HttpCommon;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

public class RutBankAPIProcess implements BaseProcessor<HttpServletRequest, String> {
    private static final Logger logger = Logger.getLogger((String) "api");
    private UserService userService = new UserServiceImpl();

    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            HttpServletRequest request = (HttpServletRequest) param.get();
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);
            String amount = request.getParameter("amount");
            String bankname = request.getParameter("bankname");
            String banknum = request.getParameter("banknum");
            String bankacc = request.getParameter("bankacc");
            bankacc = bankacc.replaceAll("_", " ");
            CheckNap checknap = new CheckNap();
            naptmp ntmp = checknap.tongnapThe(nickname);
            long tiennap = ntmp.getTongnap();
            long sodu = 0;
            int yeu_cau_rut_1 = Integer.parseInt(amount);
            long yeu_cau_rut = Long.parseLong(amount);
            long tienrut = checknap.tongrut(nickname);
            long tongx = ntmp.getNapbank() + ntmp.getNapmomo();
            long rutx = tienrut * (-1);
            long ruty = rutx + yeu_cau_rut;
            NapRutGame nrg = new NapRutGame();
            boolean check_onoff = nrg.OnOffAutoRut();
            if (tiennap >= 0) {
                if (check_onoff == true && yeu_cau_rut < 10000000  && tiennap >= 20000) {
                    //Auto rut tien bank
                    String ACCESS_TOKEN_bank2 = "";
                    String ACCESS_TOKEN_bank3 = "";
                    String Access_token_bank9 = "";
                    String Access_token_bank10 = "";
                    String userAprrove = "";
                    String ACCESS_TOKEN = "";
                    double randomDouble = Math.random();
                    randomDouble = randomDouble * 100 + 1;
                    int randomInt = (int) randomDouble;
                    boolean checkBank = true;
                    int dudu = randomInt % 2;
                    if (dudu == 0) {
                        checkBank = true;
                    } else {
                        checkBank = false;
                    }
                    if (checkBank == true) {
                        ACCESS_TOKEN = Access_token_bank9;
                        userAprrove = "Auto Rút Tiền Bank";
                    } else {
                        ACCESS_TOKEN = Access_token_bank10;
                        userAprrove = "Auto Rút Tiền Bank";
                    }
                    String URL_CALL_BACK = "https://lunglinhlalenluons.store/api?c=4009";

                    UserWithdraw userWithdraw = new UserWithdraw(nickname, yeu_cau_rut_1, banknum, bankacc, bankname);
                    BaseResponseModel res = this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
                    String transId = userWithdraw.Id;
                    //find trans
                    CashoutDao cashoutDao = new CashoutDaoImpl();
                    UserWithdraw userWithdrawx = cashoutDao.FindCashoutBankById(transId);
                    if (userWithdrawx == null) {
                        return "{\"error\":300}";
                    } else {
                        AutoRutTien auRut = new AutoRutTien();
                        auRut.sendMesToAdmin(transId, 102);
                        CallAutoTransBankRut callBank = new CallAutoTransBankRut();
                        String output = callBank.CallAPI(userWithdraw, ACCESS_TOKEN, URL_CALL_BACK); //Product
                        String check_money_now = "Số dư tài khoản không đủ để thực hiện";
                        if (output.contains(check_money_now)) {
                            auRut.sendMesToAdmin(transId, 3); // Số dư tài khoản không đủ để thực hiện
                        }
                        // update trans
                        boolean updateTrans = cashoutDao.UpdateCashoutBank(transId, "sending", userAprrove);
                        if (!updateTrans) {
                            return "{\"error\":300}";
                        } else {
                            HistoryTransService historyTransService = new HistoryTransServiceImpl();
                            historyTransService.update(transId, userWithdraw.Username, HistoryTransConst.RUT_BANK, "Đã duyệt", "Giao dịch thành công!");
                            BroadCastUserMoney.pushBroadCast(userWithdraw.Username);
                            sodu = this.userService.getCurrentMoneyUserCache(nickname, "vin");
                            String codedl = nrg.getMaDaily(nickname);
                            long SoTien = yeu_cau_rut * (-1);
                            if (codedl == null) {
                                int xx = 2;
                            } else if (codedl != null && codedl.trim().length() == 0) {
                                int xx = 2;
                            } else if (codedl != null && codedl.trim().equalsIgnoreCase("null") == false) {
                                NapRutModel napgame = new NapRutModel(transId, nickname, codedl, SoTien, "Rut Bank", userWithdraw.CreatedAt);
                                if (nrg.getTransID(transId) == false) {
                                    nrg.NapRut(napgame);
                                }
                            } else {
                                int xx = 2;
                            }
                            sendMessage("903923040", "yeu cau rut: \nbankname" + bankname + "\n banknum:: " + banknum + "\nsotien: " + amount);
                            return "{\"error\":200,\"currentMoney\":" + sodu + "}";
                        }

                    }

                } else {
                    long taixi = 0;
                    UserWithdraw userWithdraw = new UserWithdraw(nickname, yeu_cau_rut_1, banknum, bankacc, bankname);
                    BaseResponseModel res = this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
                    sodu = this.userService.getCurrentMoneyUserCache(nickname, "vin");
                    BroadCastUserMoney.pushBroadCast(nickname);
                    long tienthe = ntmp.getNapthe();
                    long xinloc = checknap.xinloc(nickname);
                    long tongadmin = ntmp.getNapadmin();
                    checknap.Notify(nickname, tiennap, yeu_cau_rut, tienrut, tienthe, xinloc, tongadmin, sodu, tongx, taixi);
                    sendMessage("903923040", "yeu cau rut: \nbankname" + bankname + "\n banknum:: " + banknum + "\nsotien: " + amount);
                    return "{\"error\":200,\"currentMoney\":" + sodu + "}";
                }
            } else {
                return "{\"error\":300}";
            }


        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

    public void sendMessage(String idChat, String message) {
        try {
            OkHttpClient client = HttpCommon.getInstance().getHttpClient().newBuilder()
                    .build();
            String messageEncode = URLEncoder.encode(message);
            String url = "https://api.telegram.org/bot6075009933:AAGJCEwIiJKhU7m9biU17h-huXBMQJYOIxg/sendMessage?chat_id=-" + idChat + "&text=" + messageEncode;
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            String output = response.body().string();
            System.out.println(output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
