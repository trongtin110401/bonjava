package com.vinplay.api.processors.rutbankapi;

import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dal.dao.ReportDAO;
import com.vinplay.dal.dao.impl.ReportDaoImpl;
import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.lognaprut.HistoryTransConst;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.entities.HistoryTransResponse;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.usercore.service.GiftCodeService;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.GiftCodeServiceImpl;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;
import com.vinplay.vbee.common.statics.Consts;
import org.apache.commons.collections.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RutBankAPIProcess implements BaseProcessor<HttpServletRequest, String> {
    private UserService userService = new UserServiceImpl();

    public synchronized String execute(Param<HttpServletRequest> param) {
        try {
            BaseResponseModel baseResponseModel;
            HttpServletRequest request = param.get();
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);
            String amount = request.getParameter("amount");
            String bankname = request.getParameter("bankname");
            String banknum = request.getParameter("banknum");
            String bankacc = request.getParameter("bankacc");
            String otp = request.getParameter("otp");
            OtpServiceImpl service = new OtpServiceImpl();
            baseResponseModel = service.checkOTP(nickname, otp);
            if (!baseResponseModel.isSuccess()) {
                return baseResponseModel.toJson();
            }



            GiftCodeService giftCodeService = new GiftCodeServiceImpl();
            if (!giftCodeService.checkUserTransactionAfterUseGiftCode(nickname)) {
                baseResponseModel = new BaseResponseModel(false, "Bạn phải phát sinh giao dịch sau khi nhập gift code.");
                return baseResponseModel.toJson();
            }

            long totalBetToday = getTotalBetToday(nickname);
            long fistRechargeValueToday = getFirstRechargeToday(nickname);
            System.out.println(" totalBetToday = " + totalBetToday + " - firstCharge = " + fistRechargeValueToday);
            if (totalBetToday <= 0 || fistRechargeValueToday <= 0 || totalBetToday < fistRechargeValueToday / 2) {
                baseResponseModel = new BaseResponseModel(false, "Bạn chưa cược đủ 50% giá trị mã nạp đầu tiên. Vui lòng cược thêm.");
                return baseResponseModel.toJson();
            }

            String type = request.getParameter("type");
            bankacc = bankacc.replaceAll("_", " ");
            CheckNap checknap = new CheckNap();
            naptmp ntmp = checknap.tongnapThe(nickname);
            long tiennap = ntmp.getTongnap();
            int yeu_cau_rut_1 = Integer.parseInt(amount);
            String id = String.valueOf(Instant.now().toEpochMilli());
            if (tiennap >= 0) {
                if ("momo".equalsIgnoreCase(type)) {
                    UserWithdrawMomo userWithdrawMomo = new UserWithdrawMomo(nickname, yeu_cau_rut_1, banknum);
                    userWithdrawMomo.setAccountName(bankacc);
                    userWithdrawMomo.Id = id;
                    baseResponseModel = this.userService.UpdateMoneyWhenWithdrawMomo(userWithdrawMomo);
                } else if ("bank".equalsIgnoreCase(type)) {
                    UserWithdraw userWithdraw = new UserWithdraw(nickname, yeu_cau_rut_1, banknum, bankacc, bankname);
                    userWithdraw.Id = id;
                    baseResponseModel = this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
                }
                BroadCastUserMoney.pushBroadCast(nickname);
                return baseResponseModel.toJson();
            }
            return baseResponseModel.toJson();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }

    private long getTotalBetToday(String nickname) {
//        Map<String, ReportMoneySystemModel> actions = new HashMap();
//        HazelcastInstance client = HazelcastClientFactory.getInstance();
//        String today = VinPlayUtils.getCurrentDate();
//        ReportModel model;
//        String actionname;
//        IMap<String, ReportModel> reportMap = client.getMap("cacheReports");
//        for (IMap.Entry entry : reportMap.entrySet()) {
//            ReportMoneySystemModel reportMoneySystemModel;
//            if (!((String) entry.getKey()).contains(today)) continue;
//            String[] arr = ((String) entry.getKey()).split(",");
//            String nickname2 = arr[0];
//            actionname = arr[1];
//            if (!nickname2.equals(nickname)) continue;
//            model = (ReportModel) entry.getValue();
//            ReportMoneySystemModel rModel = reportMoneySystemModel = new ReportMoneySystemModel();
//            reportMoneySystemModel.moneyWin += model.moneyWin;
//            ReportMoneySystemModel reportMoneySystemModel2 = rModel;
//            reportMoneySystemModel2.moneyLost += model.moneyLost;
//            ReportMoneySystemModel reportMoneySystemModel3 = rModel;
//            reportMoneySystemModel3.moneyOther += model.moneyOther;
//            ReportMoneySystemModel reportMoneySystemModel4 = rModel;
//            reportMoneySystemModel4.fee += model.fee;
//            ReportMoneySystemModel reportMoneySystemModel5 = rModel;
//            reportMoneySystemModel5.revenuePlayGame += model.moneyWin + model.moneyLost;
//            ReportMoneySystemModel reportMoneySystemModel6 = rModel;
//            reportMoneySystemModel6.revenue += model.moneyWin + model.moneyLost + model.moneyOther;
//            actions.put(actionname, rModel);
//        }

        // Define the format you want for the date-time strings
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Get the current date
        LocalDate fromDate = LocalDate.parse("2024-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Get the start of the day (00:00:00)
        LocalDateTime startOfDay = fromDate.atStartOfDay();
        String startTime = startOfDay.format(formatter);

        // Get the end of the day (23:59:59)
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        String endTime = endOfDay.format(formatter);

        ReportDaoImpl reportDao = new ReportDaoImpl();
        Map<String, ReportMoneySystemModel> actions = reportDao.getReportMoneyUser2(startTime, endTime, nickname, false);

        long totalBetToday = 0l;
        for (Map.Entry<String, ReportMoneySystemModel> entry : actions.entrySet()) {
            if (Consts.NO_GAME.contains(entry.getKey())) {
                continue;
            }

            totalBetToday += (entry.getValue().moneyLost * -1);
        }
        return totalBetToday;
    }

    private long getFirstRechargeToday(String nickname) {
        // Define the format you want for the date-time strings
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Get the current date
        LocalDate fromDate = LocalDate.parse("2024-01-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Get the start of the day (00:00:00)
        LocalDateTime startOfDay = fromDate.atStartOfDay();
        String startTime = startOfDay.format(formatter);

        // Get the end of the day (23:59:59)
        LocalDateTime endOfDay = LocalDate.now().atTime(23, 59, 59);
        String endTime = endOfDay.format(formatter);

        HistoryTransDao historyTransDao = new HistoryTransDaoImpl();
        HistoryTransResponse res = historyTransDao.getListTransByDay(nickname, startTime, endTime);

        if (CollectionUtils.isNotEmpty(res.getListTrans())) {
            // ??o ng??c list ?? l?y th?i gian t? th?p t?i cao
            Collections.reverse(res.getListTrans());
            Optional<HistoryTransModel> optional = res.getListTrans().stream().filter(historyTransModel ->
                            historyTransModel.hinhthucTrans.equals(HistoryTransConst.MOMO)
                                    || historyTransModel.hinhthucTrans.equals(HistoryTransConst.BANK)
                                    || historyTransModel.hinhthucTrans.equals(HistoryTransConst.CARD))

                    .findFirst();
            if (optional.isPresent()) {
                return Long.parseLong(optional.get().sotien);
            }
        }
        return 0;
    }

}


