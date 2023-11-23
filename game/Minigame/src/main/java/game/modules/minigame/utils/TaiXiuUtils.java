/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.server.api.IBZApi
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.util.ExtensionUtility
 *  com.vinplay.dal.dao.impl.TaiXiuDAOImpl
 *  com.vinplay.dal.entities.taixiu.ResultTaiXiu
 *  com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.models.cache.ThanhDuTXModel
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.statics.TransType
 *  org.apache.log4j.Logger
 */
package game.modules.minigame.utils;

import bitzero.server.api.IBZApi;
import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.ExtensionUtility;
import com.vinplay.dal.dao.impl.TaiXiuDAOImpl;
import com.vinplay.dal.entities.taixiu.ResultTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.models.cache.ThanhDuTXModel;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import game.modules.minigame.cmd.send.UpdateUserInfoMsg;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class TaiXiuUtils {
    private static Logger loggerTaiXiu = Logger.getLogger((String)"csvBetTaiXiu");
    private static final String FORMAT_LOG_BET_TAI_XIU = "%d,\t%s,\t%d,\t%d,\t%d,\t%s,\t%s";
    private static Logger loggerThanhDu = Logger.getLogger((String)"csvThanhDuPrize");
    private static long[] prizes = new long[]{500000L, 200000L, 100000L};
    private static final String FORMAT_THANH_DU = "%s,\t%d,\t%d,\t%d,\t%d,\t%d,\t%d,\t%s";

    public static String buildLichSuPhien(List<ResultTaiXiu> input, int number) {
        int end = input.size();
        int start = end - number > 0 ? end - number : 0;
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; ++i) {
            ResultTaiXiu entry = input.get(i);
            builder.append(entry.dice1);
            builder.append(",");
            builder.append(entry.dice2);
            builder.append(",");
            builder.append(entry.dice3);
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public static String logLichSuPhien(List<ResultTaiXiu> input, int number) {
        int end = input.size();
        int start = end - number > 0 ? end - number : 0;
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < end; ++i) {
            ResultTaiXiu entry = input.get(i);
            builder.append(entry.result);
            builder.append(",");
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        return builder.toString();
    }

    public static void rewardThanhDu() {
        try {
            SimpleDateFormat startTimeFormat = new SimpleDateFormat("yyyy/MM/dd 00:00:00");
            SimpleDateFormat endTimeFormat = new SimpleDateFormat("yyyy/MM/dd 23:59:59");
            Calendar cal = Calendar.getInstance();
            cal.add(5, -1);
            String startTime = startTimeFormat.format(cal.getTime());
            String endTime = endTimeFormat.format(cal.getTime());
            TaiXiuUtils.rewardThanhDu(startTime, endTime, (short)1);
            TaiXiuUtils.rewardThanhDu(startTime, endTime, (short)0);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void rewardThanhDu(String startTime, String endTime, short type) throws SQLException {
        long moneyAfterUpdated;
        ThanhDuTXModel entry;
        String actionName = "Tr\u00e1\u00ba\u00a3 th\u00c6\u00b0\u00e1\u00bb\u0178ng Th\u00c3\u00a1nh D\u00e1\u00bb\u00b1 top " + (type == 1 ? "thua" : "th\u00e1\u00ba\u00afng");
        TaiXiuDAOImpl txDAO = new TaiXiuDAOImpl();
        List listUser = txDAO.getTopThanhDuDaily(startTime, endTime, type);
        int rank = 0;
        if (listUser.size() > 0) {
            entry = (ThanhDuTXModel)listUser.get(rank);
           // moneyAfterUpdated = TaiXiuUtils.rewardThanhDuToUser(entry, prizes[rank], actionName);
           // TaiXiuUtils.log(entry.username, rank + 1, type, entry.number, entry.totalValue, moneyAfterUpdated, prizes[rank]);
            ++rank;
        }
        if (listUser.size() > 1) {
            entry = (ThanhDuTXModel)listUser.get(rank);
         //   moneyAfterUpdated = TaiXiuUtils.rewardThanhDuToUser(entry, prizes[rank], actionName);
         //   TaiXiuUtils.log(entry.username, rank + 1, type, entry.number, entry.totalValue, moneyAfterUpdated, prizes[rank]);
            ++rank;
        }
        if (listUser.size() > 2) {
            entry = (ThanhDuTXModel)listUser.get(rank);
          //  moneyAfterUpdated = TaiXiuUtils.rewardThanhDuToUser(entry, prizes[rank], actionName);
        //    TaiXiuUtils.log(entry.username, rank + 1, type, entry.number, entry.totalValue, moneyAfterUpdated, prizes[rank]);
            ++rank;
        }
    }

    private static long rewardThanhDuToUser(ThanhDuTXModel entry, long prize, String actionName) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        UserServiceImpl userService = new UserServiceImpl();
        Calendar cal = Calendar.getInstance();
        cal.add(5, -1);
        MoneyResponse response = userService.updateMoney(entry.username, prize, "vin", "TaiXiu", "Th\u00c3\u00a1nh D\u00e1\u00bb\u00b1 - Tr\u00e1\u00ba\u00a3 th\u00c6\u00b0\u00e1\u00bb\u0178ng", "Ng\u00c3\u00a0y " + format.format(cal.getTime()), 0L, null, TransType.NO_VIPPOINT);
        List<User> u = ExtensionUtility.getExtension().getApi().getUserByName(entry.username);
        if (response != null && response.isSuccess() && u != null) {
            UpdateUserInfoMsg msg = new UpdateUserInfoMsg();
            msg.currentMoney = response.getCurrentMoney();
            msg.moneyType = 1;
            ExtensionUtility.getExtension().send(msg, u.get(0));
        }
        if (response != null) {
            return response.getCurrentMoney();
        }
        return -1L;
    }

    private static void log(String username, int rank, int type, int number, long totalValue, long prize, long moneyAfterUpdated) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String str = String.format(FORMAT_THANH_DU, username, rank, type, number, totalValue, prize, moneyAfterUpdated, df.format(new Date()));
        loggerThanhDu.debug((Object)str);
    }
    //todo : log bet tài xỉu
    public static void logBetTaiXiu(TransactionTaiXiuDetail tran) {
        String moneyType = tran.moneyType == 1 ? "vin" : "xu";
        TaiXiuUtils.logBetTaiXiu(tran.referenceId, tran.username, tran.betValue, tran.betSide, tran.inputTime, moneyType);
    }
    // todo: log bet tài xỉu
    public static void logBetTaiXiu(long referenceId, String nickname, long betValue, int betSide, int inputTime, String moneyType) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        String str = String.format(FORMAT_LOG_BET_TAI_XIU, referenceId, nickname, betValue, betSide, inputTime, moneyType, df.format(new Date()));
        loggerTaiXiu.debug((Object)str);
    }
}

