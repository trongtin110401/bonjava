package com.vinplay.api.processors.rutbankapi;

import com.vinplay.dal.common.BroadCastUserMoney;
import com.vinplay.dal.dao.impl.StatMoneyInOutDaoImpl;
import com.vinplay.dal.entities.report.StatMoneyInOut;
import com.vinplay.lognaprut.HistoryTransDao;
import com.vinplay.lognaprut.entities.HistoryTransModel;
import com.vinplay.lognaprut.impl.HistoryTransDaoImpl;
import com.vinplay.payment.entities.UserWithdraw;
import com.vinplay.payment.entities.UserWithdrawMomo;
import com.vinplay.usercore.service.OtherService;
import com.vinplay.usercore.service.UserExtraService;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.OtherServiceImpl;
import com.vinplay.usercore.service.impl.OtpServiceImpl;
import com.vinplay.usercore.service.impl.UserExtraServiceImpl;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;
import com.vinplay.vbee.common.response.BaseResponseModel;
import org.bson.Document;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class RutBankAPIProcess implements BaseProcessor<HttpServletRequest, String> {
    private UserService userService = new UserServiceImpl();

    public synchronized String execute(Param<HttpServletRequest> param) {
        Document document = new Document();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        document.put("created_date", dateFormat.format(date));
        try {
            BaseResponseModel baseResponseModel = new BaseResponseModel(false, "1001");
            HttpServletRequest request = param.get();
            String accessToken = request.getParameter("at");
            String nickname = this.getUserNameByAccessToken(accessToken);
            if (nickname == null) {
                document.put("token", "invalid");
                return baseResponseModel.toJson();
            }
            String amount = request.getParameter("amount");
            String bankname = request.getParameter("bankname");
            String banknum = request.getParameter("banknum");
            String bankacc = request.getParameter("bankacc");
            String otp = request.getParameter("otp");
            OtpServiceImpl service = new OtpServiceImpl();
            document.put("nick_name", nickname);
            baseResponseModel = service.checkOTP(nickname, otp);
            if (!baseResponseModel.isSuccess()) {
                document.put("OTP", false);
                return baseResponseModel.toJson();
            }else {
                service.clearOTPPhone(nickname);
                service.clearOTPTele(nickname);
            }
            document.put("bank_name", bankname);
            document.put("amount", amount);
            document.put("OTP", true);
            // Kiem tra dieu kien rut
            StatMoneyInOutDaoImpl moneyInOutDao = StatMoneyInOutDaoImpl.getInstance();
            StatMoneyInOut moneyInOut = moneyInOutDao.find(nickname);
            long totalBetValue = moneyInOut.totalBetValue;
            long totalDepositGiftcode = moneyInOut.depositGiftcode;
            long firstRechargeValue = getFirstRechargeValue(nickname);

            long moneyNeededForWithdrawal = calculateMoneyNeededForWithdrawal(firstRechargeValue, totalDepositGiftcode, totalBetValue);

            if (moneyNeededForWithdrawal > 0) {
                String message = buildWithdrawalMessage(firstRechargeValue, totalDepositGiftcode, totalBetValue, moneyNeededForWithdrawal);
                baseResponseModel = new BaseResponseModel(false, message);
                document.put("money_need_for_withdrawal", moneyNeededForWithdrawal);
                return baseResponseModel.toJson();
            }

            // dieu kien rut thoa man. gui lenh rut
            String type = request.getParameter("type");
            bankacc = bankacc.replaceAll("_", " ");
            CheckNap checknap = new CheckNap();
            naptmp ntmp = checknap.tongnapThe(nickname);
            long tiennap = ntmp.getTongnap();
            int yeu_cau_rut_1 = Integer.parseInt(amount);
            String id = String.valueOf(Instant.now().toEpochMilli());
            if (tiennap >= 0) {
                if ("momo".equalsIgnoreCase(type)) {
                    document.put("type", "momo");
                    UserWithdrawMomo userWithdrawMomo = new UserWithdrawMomo(nickname, yeu_cau_rut_1, banknum);
                    userWithdrawMomo.setAccountName(bankacc);
                    userWithdrawMomo.Id = id;
                    baseResponseModel = this.userService.UpdateMoneyWhenWithdrawMomo(userWithdrawMomo);
                } else if ("bank".equalsIgnoreCase(type)) {
                    document.put("type", "bank");
                    UserWithdraw userWithdraw = new UserWithdraw(nickname, yeu_cau_rut_1, banknum, bankacc, bankname);
                    userWithdraw.Id = id;
                    baseResponseModel = this.userService.UpdateMoneyWhenWithdrawBank(userWithdraw);
                }
                BroadCastUserMoney.pushBroadCast(nickname);
                document.put("status", baseResponseModel.isSuccess());
                return baseResponseModel.toJson();
            }
            return baseResponseModel.toJson();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {
            OtherService otherService = new OtherServiceImpl();
            otherService.saveUserCashOutTransaction(document);
        }
    }

    private String formatCurrencyVND(long amount) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(localeVN);
        return currencyFormatter.format(amount);
    }

    private long calculateMoneyNeededForWithdrawal(long firstRechargeValue, long totalDepositGiftcode, long totalBetValue) {
        return (long) ((firstRechargeValue * 0.5) + totalDepositGiftcode * 2 - totalBetValue);
    }

    private String buildWithdrawalMessage(long firstRechargeValue, long totalDepositGiftcode, long totalBetValue, long moneyNeeded) {
        String formattedFirstRechargeValue = formatCurrencyVND(firstRechargeValue);
        String formattedTotalDepositGiftcode = formatCurrencyVND(totalDepositGiftcode);
        String formattedTotalBetValue = formatCurrencyVND(totalBetValue);
        String formattedMoneyNeeded = formatCurrencyVND(moneyNeeded);
        return String.format(
                "Mã nạp đầu : %s.\nNạp Giftcode : %s.\nTổng cược : %s.\nBạn cần cược thêm : %s để rút.",
                formattedFirstRechargeValue, formattedTotalDepositGiftcode, formattedTotalBetValue, formattedMoneyNeeded
        );
    }

    private String getUserNameByAccessToken(String accessToken) {
        UserExtraService userExtraService = new UserExtraServiceImpl();
        return userExtraService.getModelFromToken(accessToken).getNickname();
    }


    private long getFirstRechargeValue(String nickname) {
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
        HistoryTransModel res = historyTransDao.getFirstTrans(nickname, startTime);
//        if (CollectionUtils.isNotEmpty(res.getListTrans())) {
//            Collections.reverse(res.getListTrans());
//            Optional<HistoryTransModel> optional = res.getListTrans().stream().filter(historyTransModel ->
//                            (Integer.parseInt(historyTransModel.getSotien()) > 0 && (historyTransModel.hinhthucTrans.equals(HistoryTransConst.MOMO)
//                                    || historyTransModel.hinhthucTrans.equals(HistoryTransConst.BANK)
//                                    || historyTransModel.hinhthucTrans.equals(HistoryTransConst.CARD))))
//                    .findFirst();
//            if (optional.isPresent()) {
//                return Long.parseLong(optional.get().sotien);
//            }
//        }
        if (res != null) {
            return Long.parseLong(res.getSotien());
        }
        return 0;
    }

}


