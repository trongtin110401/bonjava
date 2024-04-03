package com.vinplay.api.processors.rutbankapi;

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
import com.vinplay.utils.TelegramUtil;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;

public class RutBankAPIProcess implements BaseProcessor<HttpServletRequest, String> {
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
            NapRutGame nrg = new NapRutGame();
            boolean check_onoff = nrg.OnOffAutoRut();
            if (tiennap >= 0) {
                if (check_onoff && yeu_cau_rut < 10000000 && tiennap >= 20000) {
                    //Auto rut tien bank
                    String userAprrove = "Auto Rút Tiền Bank";
                    double randomDouble = Math.random();
                    randomDouble = randomDouble * 100 + 1;
                    int randomInt = (int) randomDouble;
                    int dudu = randomInt % 2;

                    String URL_CALL_BACK = "https://lunglinhlalenluons.store/api?c=4009";

                    UserWithdraw userWithdraw = new UserWithdraw(nickname, yeu_cau_rut_1, banknum, bankacc, bankname);
                    this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
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
                        String output = callBank.CallAPI(userWithdraw, URL_CALL_BACK); //Product
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
                            } else if (codedl.trim().length() == 0) {
                                int xx = 2;
                            } else if (!codedl.trim().equalsIgnoreCase("null")) {
                                NapRutModel napgame = new NapRutModel(transId, nickname, codedl, SoTien, "Rut Bank", userWithdraw.CreatedAt);
                                if (!nrg.getTransID(transId)) {
                                    nrg.NapRut(napgame);
                                }
                            } else {
                                int xx = 2;
                            }
                            sendMessage("yeu cau rut: \nbankname" + bankname + "\n banknum:: " + banknum + "\nsotien: " + amount);
                            return "{\"error\":200,\"currentMoney\":" + sodu + "}";
                        }

                    }

                } else {
                    long taixi = 0;
                    UserWithdraw userWithdraw = new UserWithdraw(nickname, yeu_cau_rut_1, banknum, bankacc, bankname);
                    this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
                    sodu = this.userService.getCurrentMoneyUserCache(nickname, "vin");
                    BroadCastUserMoney.pushBroadCast(nickname);
                    long tienthe = ntmp.getNapthe();
                    long xinloc = checknap.xinloc(nickname);
                    long tongadmin = ntmp.getNapadmin();
                    checknap.Notify(nickname, tiennap, yeu_cau_rut, tienrut, tienthe, xinloc, tongadmin, sodu, tongx, taixi);
                    sendMessage("yeu cau rut: \nbankname" + bankname + "\n banknum:: " + banknum + "\nsotien: " + amount);
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

    public void sendMessage(String message) {
        try {
            String messageEncode = URLEncoder.encode(message);
            TelegramUtil telegramUtil = new TelegramUtil();
            telegramUtil.sendMessageNapRut(messageEncode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
