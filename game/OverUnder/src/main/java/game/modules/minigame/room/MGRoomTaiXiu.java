/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.server.entities.User
 *  bitzero.server.extensions.data.BaseMsg
 *  bitzero.util.common.business.Debug
 *  com.vinplay.dal.entities.report.ReportMoneySystemModel
 *  com.vinplay.dal.entities.taixiu.ResultTaiXiu
 *  com.vinplay.dal.entities.taixiu.TransactionTaiXiu
 *  com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail
 *  com.vinplay.dal.service.BroadcastMessageService
 *  com.vinplay.dal.service.TaiXiuService
 *  com.vinplay.dal.service.impl.BroadcastMessageServiceImpl
 *  com.vinplay.dal.service.impl.TaiXiuServiceImpl
 *  com.vinplay.usercore.service.UserService
 *  com.vinplay.usercore.service.impl.UserServiceImpl
 *  com.vinplay.vbee.common.enums.Games
 *  com.vinplay.vbee.common.models.cache.UserCacheModel
 *  com.vinplay.vbee.common.response.MoneyResponse
 *  com.vinplay.vbee.common.statics.TransType
 */
package game.modules.minigame.room;

import bitzero.server.entities.User;
import bitzero.server.extensions.data.BaseMsg;
import bitzero.util.common.business.Debug;
import com.vinplay.dal.entities.taixiu.ResultTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail;
import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.OverUnderService;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.OverUnderServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import game.modules.minigame.cmd.rev.BetTaiXiuCmd;
import game.modules.minigame.cmd.send.BetTaiXiuMsg;
import game.modules.minigame.cmd.send.TaiXiuInfoMsg;
import game.modules.minigame.cmd.send.UpdatePrizeTaiXiuMsg;
import game.modules.minigame.cmd.send.UpdateResultDicesMsg;
import game.modules.minigame.cmd.send.UpdateRutLocMsg;
import game.modules.minigame.cmd.send.UpdateTaiXiuPerSecondMsg;
import game.modules.minigame.entities.BalanceMoneyTX;
import game.modules.minigame.entities.MinigameConstant;
import game.modules.minigame.entities.PotTaiXiu;
import game.modules.minigame.room.MGRoom;
import game.modules.minigame.utils.RutLocUtils;
import game.modules.minigame.utils.TaiXiuUtils;
import game.utils.ConfigGame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class MGRoomTaiXiu
extends MGRoom {
    public long referenceId;
    private short moneyType;
    private String moneyTypeStr;
    private PotTaiXiu potTai;
    private PotTaiXiu potXiu;
    private long startTime = 0L;
    private short result = (short)-1;
    public boolean bettingRound = false;
    public boolean enableBetting = false;
    private ResultTaiXiu resultTX;
    private OverUnderService api = new OverUnderServiceImpl();
    private UserService userService = new UserServiceImpl();
    private BroadcastMessageService broadcastMsgService = new BroadcastMessageServiceImpl();
    private float tax = MinigameConstant.MINIGAME_TAX_VIN;
    private BalanceMoneyTX balance = new BalanceMoneyTX();
    private long blackListBetTai = 0L;
    private long blackListBetXiu = 0L;
    private long whiteListBetTai = 0L;
    private long whiteListBetXiu = 0L;
    public long nguoichoidatTai = 0L;
    public long nguoichoidatXiu = 0L;

    public MGRoomTaiXiu(String name, long referenceId, short moneyType) {
        super(name);
        this.moneyType = moneyType;
        this.moneyTypeStr = "xu";
        if (moneyType == 1) {
            this.moneyTypeStr = "vin";
            this.tax = MinigameConstant.MINIGAME_TAX_VIN;
            ReportMoneySystemModel model = this.api.getReportTX(ConfigGame.getIntValue("interval_reset_balance", 10));
            if (model != null) {
                this.balance = new BalanceMoneyTX(model.moneyWin, model.moneyLost, model.fee, model.dateReset);
                Debug.trace((Object)("TAI XIU VIN, win=" + model.moneyWin + ", loss=" + model.moneyLost + ", fee= " + model.fee + ", date reset= " + model.dateReset));
            }
        } else {
            this.tax = MinigameConstant.MINIGAME_TAX_XU;
        }
        this.potTai = new PotTaiXiu();
        this.potXiu = new PotTaiXiu();
        this.referenceId = referenceId;
    }

    public void startNewGame(long newReferenceId) {
        this.referenceId = newReferenceId;
        this.bettingRound = true;
        this.enableBetting = true;
        this.blackListBetTai = 0L;
        this.blackListBetXiu = 0L;
        this.whiteListBetTai = 0L;
        this.whiteListBetXiu = 0L;
        this.nguoichoidatTai = 0L;
        this.nguoichoidatXiu = 0L;
        this.potTai.renew();
        this.potXiu.renew();
        this.startTime = System.currentTimeMillis();
        Debug.trace((Object)("START NEW ROUND " + this.referenceId));
    }

    public void finish() {
        this.resultTX = null;
        this.startTime = System.currentTimeMillis();
        this.bettingRound = false;
    }

    public void disableBetting() {
        this.enableBetting = false;
    }

    public void updateResultDices(short[] dices, short result) {
        this.result = result;
        UpdateResultDicesMsg msg = new UpdateResultDicesMsg();
        msg.result = result;
        msg.dice1 = dices[0];
        msg.dice2 = dices[1];
        msg.dice3 = dices[2];
        this.resultTX = new ResultTaiXiu();
        this.resultTX.referenceId = this.referenceId;
        this.resultTX.dice1 = msg.dice1;
        this.resultTX.dice2 = msg.dice2;
        this.resultTX.dice3 = msg.dice3;
        this.resultTX.result = msg.result;
        this.resultTX.moneyType = this.moneyType;
        this.sendMessageToRoom(msg);
    }

    public short getRemainTime() {
        long currentTime = System.currentTimeMillis();
        int remainTime = (int)((currentTime - this.startTime) / 1000L);
        if (remainTime < 0) {
            remainTime = 0;
        } else if (remainTime > 60) {
            remainTime = 60;
        }
        if (this.bettingRound) {
            return (short)(60 - remainTime);
        }
        return (short)(30 - remainTime);
    }

    public void betTaiXiu(User user, BetTaiXiuCmd cmd) {
        BetTaiXiuMsg msg = this.betTaiXiu(user.getName(), cmd.userId, cmd.betValue, cmd.inputTime, cmd.moneyType, cmd.betSide, false);
        this.sendMessageToUser((BaseMsg)msg, user);
    }

    public BetTaiXiuMsg betTaiXiu(String nickname, int userId, long betValue, short inputTime, short moneyType, short betSide, boolean isBot) {
        if (!isBot && moneyType == 1) {
            if (betSide == 1) {
                this.nguoichoidatTai += betValue;
            } else {
                this.nguoichoidatXiu += betValue;
            }
        }
        inputTime = this.getRemainTime();
        long currentMoney = this.userService.getMoneyUserCache(nickname, this.moneyTypeStr);
        int result = 2;
        if (this.enableBetting) {
            if (betValue >= 100L) {
                if (betValue > currentMoney) {
                    result = 3;
                } else {
                    TransactionTaiXiuDetail transTX = new TransactionTaiXiuDetail(this.referenceId, userId, nickname, betValue, (int)betSide, (int)inputTime, (int)moneyType);
                    if (betSide == 1 && this.potXiu.getTotalBetByUsername(nickname) > 0L || betSide == 0 && this.potTai.getTotalBetByUsername(nickname) > 0L) {
                        result = 5;
                    } else {
                        String betSideStr = betSide == 0 ? "X\u1ec9u" : "T\u00e0i";
                        MoneyResponse res = this.userService.updateMoney(nickname, -betValue, this.moneyTypeStr, "OverUnder", "T\u00e0i x\u1ec9u: \u0110\u1eb7t c\u01b0\u1ee3c", "Phii\u00ean " + this.referenceId + ": \u0111\u1eb7t " + betSideStr + " (" + inputTime + ")", 0L, Long.valueOf(this.referenceId), TransType.START_TRANS);
                        if (res.isSuccess()) {
                            if (!this.enableBetting) {
                                result = 1;
                                this.userService.updateMoney(nickname, betValue, this.moneyTypeStr, "OverUnder", "T\u00e0i x\u1ec9u: Tr\u1ea3 c\u01b0\u1ee3c", "Ho\u00e0n tr\u1ea3 \u0111\u1eb7t c\u01b0\u1ee3c phi\u00ean " + this.referenceId, 0L, Long.valueOf(this.referenceId), TransType.END_TRANS);
                            } else {
                                isBot = this.isBot(nickname);
                                if (moneyType == 1 && !isBot) {
                                    Random rd;
                                    int n;
                                    this.balance.addBet(betValue);
                                    if (betValue >= (long)ConfigGame.getIntValue("tx_min_money_black_list", 2000000) && ConfigGame.inBlackList(nickname) && (n = (rd = new Random()).nextInt(100)) <= ConfigGame.getIntValue("tx_black_list_percent", 50)) {
                                        Debug.trace((Object)("Black list " + nickname + " money= " + betValue + ", bet side= " + betSide));
                                        if (betSide == 1) {
                                            this.blackListBetTai += betValue;
                                        } else {
                                            this.blackListBetXiu += betValue;
                                        }
                                    }
                                    if (betValue >= (long)ConfigGame.getIntValue("tx_min_money_white_list", 2000000) && ConfigGame.inWhiteList(nickname) && (n = (rd = new Random()).nextInt(100)) <= ConfigGame.getIntValue("tx_white_list_percent", 50)) {
                                        Debug.trace((Object)("White list " + nickname + " money= " + betValue + ", bet side= " + betSide));
                                        if (betSide == 1) {
                                            this.whiteListBetTai += betValue;
                                        } else {
                                            this.whiteListBetXiu += betValue;
                                        }
                                    }
                                }
                                if (betSide == 1) {
                                    this.potTai.bet(transTX, isBot);
                                } else {
                                    this.potXiu.bet(transTX, isBot);
                                }
                                currentMoney = res.getCurrentMoney();
                                transTX.genTransactionCode();
                                if (!isBot) {
                                    TaiXiuUtils.logBetTaiXiu(transTX);
                                }
                                result = 0;
                            }
                        } else {
                            result = 1;
                        }
                    }
                }
            } else {
                result = 4;
            }
        }
        BetTaiXiuMsg msg = new BetTaiXiuMsg();
        msg.Error = (byte)result;
        msg.currentMoney = currentMoney;
        return msg;
    }

    public void updateTaiXiuPerSecond() {
        UpdateTaiXiuPerSecondMsg msg = new UpdateTaiXiuPerSecondMsg();
        msg.remainTime = this.getRemainTime();
        msg.bettingState = this.bettingRound;
        msg.potTai = this.getPotTai();
        msg.potXiu = this.getPotXiu();
        msg.numBetTai = this.potTai.getNumBet();
        msg.numBetXiu = this.potXiu.getNumBet();
        this.sendMessageToRoom(msg);
    }

    public short suggestResult() {
        if(this.moneyType != 1) return -1;

        PotTaiXiu potX = this.potXiu;
        PotTaiXiu potT = this.potTai;
        long tongTienHopLe = (potT == null || potX == null) ? 0 : (potT.getTotalValue() > potX.getTotalValue() ? potX.getTotalValue() : potT.getTotalValue());
        long tongTienTaiDaTinh = 0L;
        long tongTienXiuDaTinh = 0L;

        long totalCashIn = 0L;
        long totalCashOut = 0L;

        // xiu win
        if (potX != null && potX.contributors != null) {
            for (TransactionTaiXiuDetail tran : potX.contributors) {
                if(tran.userId == 0) continue;
                long tienDuocTinh = tran.betValue;
                totalCashIn += tienDuocTinh;
                if (tongTienXiuDaTinh + tran.betValue > tongTienHopLe) {
                    tienDuocTinh = tongTienHopLe - tongTienXiuDaTinh;
                }
                tongTienXiuDaTinh += tienDuocTinh;
                totalCashOut += (long)((float)tienDuocTinh * (100.0f - this.tax) / 100.0f) + tienDuocTinh;
                totalCashOut += tran.betValue - tienDuocTinh;
            }
        }
        if (!(potT == null || potT.contributors == null))
        for (TransactionTaiXiuDetail tran : potT.contributors) {
            if(tran.userId == 0) continue;
            long tienDuocTinh = tran.betValue;
            totalCashIn += tienDuocTinh;
            if (tongTienTaiDaTinh + tran.betValue > tongTienHopLe) {
                tienDuocTinh = tongTienHopLe - tongTienTaiDaTinh;
            }
            tongTienTaiDaTinh += tienDuocTinh;
            totalCashOut += tran.betValue - tienDuocTinh;
        }
        long deltaXiu = totalCashIn - totalCashOut;

        totalCashIn = 0L;
        totalCashOut = 0L;
        // tai win                
        if (potT != null && potT.contributors != null) {
            for (TransactionTaiXiuDetail tran : potT.contributors) {
                if(tran.userId == 0) continue;
                long tienDuocTinh = tran.betValue;
                totalCashIn += tienDuocTinh;
                if (tongTienTaiDaTinh + tran.betValue > tongTienHopLe) {
                    tienDuocTinh = tongTienHopLe - tongTienTaiDaTinh;
                }
                tongTienTaiDaTinh += tienDuocTinh;
                totalCashOut += (long)((float)tienDuocTinh * (100.0f - this.tax) / 100.0f) + tienDuocTinh;
                totalCashOut += tran.betValue - tienDuocTinh;
            }
        }
        if (!(potX == null || potX.contributors == null))
        for (TransactionTaiXiuDetail tran : potX.contributors) {
            if(tran.userId == 0) continue;
            long tienDuocTinh = tran.betValue;
            totalCashIn += tienDuocTinh;
            if (tongTienXiuDaTinh + tran.betValue > tongTienHopLe) {
                tienDuocTinh = tongTienHopLe - tongTienXiuDaTinh;
            }
            tongTienXiuDaTinh += tienDuocTinh;
            totalCashOut += tran.betValue - tienDuocTinh;
        }
        long deltaTai = totalCashIn - totalCashOut;

        try {
            HazelcastInstance client = HazelcastClientFactory.getInstance();
            IMap bankMap = client.getMap("txBank2");
            String key = "txBank2:" + this.moneyType;
            long bank = 0L;
            //bankMap.lock(key);
            if (bankMap.containsKey(key)) {
                bank = (long)bankMap.get(key);
            } 
            else {
                bankMap.put(key, 0L);
            }

            bankMap.put("deltaXiu", deltaXiu);
            bankMap.put("deltaTai", deltaTai);

            //bankMap.unlock(key);
            Debug.trace((Object)("tx2 bank: " + bank));
            // enough bank
            if((deltaXiu > 0 && deltaTai > 0) || (deltaXiu < 0 && bank + deltaXiu >= 0) || (deltaTai < 0 && bank + deltaTai >= 0))
            {
                return (short)(ThreadLocalRandom.current().nextInt(0, 1000560000) % 2);
            }
            if(deltaXiu > 0 && deltaTai < 0) // let xiu win
            {
                return 0;
            }
            if(deltaXiu < 0 && deltaTai > 0) // let tai win
            {
                return 1;
            }
        }
        catch (Exception ex) {
            Debug.trace((Object)(" error resultTX: " + ex.getMessage()));
        }

        return (short)(ThreadLocalRandom.current().nextInt(0, 1000560000) % 2); // random
    }

    public void calculatePrize(long referenceId) {
        PotTaiXiu potX = this.potXiu;
        PotTaiXiu potT = this.potTai;
        HashMap<String, TransactionTaiXiu> sumTXTMap = new HashMap<String, TransactionTaiXiu>();
        HashMap<String, TransactionTaiXiu> sumTai = new HashMap<String, TransactionTaiXiu>();
        HashMap<String, TransactionTaiXiu> sumXiu = new HashMap<String, TransactionTaiXiu>();
        long tongTienHopLe = (potT == null || potX == null) ? 0 : (potT.getTotalValue() > potX.getTotalValue() ? potX.getTotalValue() : potT.getTotalValue());
        long tongTienTaiDaTinh = 0L;
        long tongTienXiuDaTinh = 0L;

        long totalCashIn = 0L;
        long totalCashOut = 0L;
        ResultTaiXiu rs = new ResultTaiXiu();
        try {
            if (this.resultTX != null) {
                rs = this.resultTX;
                Debug.trace((Object)("resultTX " + (Object)this.resultTX));
            } else {
                Debug.trace((Object)(" error: " + (Object)this.resultTX));
            }
        }
        catch (Exception ex) {
            Debug.trace((Object)(" error resultTX: " + ex.getMessage()));
        }
        switch (this.result) {
            case 0: {
                if (potX != null && potX.contributors != null) {
                    for (TransactionTaiXiuDetail tran : potX.contributors) {
                        try {
                            //số tiền mà người chơi sẽ phải bỏ vào tính thắng thua
                            long tienDuocTinh = tran.betValue;
                            // Nếu không phải robot thì tính tổng tiền đầu vào của khách thật
                            if(tran.userId != 0)
                                totalCashIn += tienDuocTinh;
                            // Nếu Tổng số tiền đã được cộng , cộng với giá trị của trans mới mà lớn hơn
                            // Số tiền hợp lệ đã chốt phiên thì số tiền được tính vào phiên của người chơi
                            // sẽ bằng tiền hợp lệ trừ đi tổng tiền đã được tính của cửa
                            // ví dụ tiền tổng đã tính là 450k + tiền mới trans là 60k, mà tiền hợp lệ là 500k thì tiền
                            // được tính là 500k - 450k = 50k và trả lại khách là 10k
                            if (tongTienXiuDaTinh + tran.betValue > tongTienHopLe) {
                                tienDuocTinh = tongTienHopLe - tongTienXiuDaTinh;
                            }
                            tongTienXiuDaTinh += tienDuocTinh;
                            //Số tiền người chơi nhận lại được nếu thắng
                            // Được tính theo công thức là tiền đặt + (% ăn nhân với tiền đặt)
                            // ví dụ người chơi đặt 100k thì nhận được 100k + ( 98 * 100) = 198k => chích 2% cho nhà cái
                            tran.prize = (long)((float)tienDuocTinh * (100.0f - this.tax) / 100.0f) + tienDuocTinh;
                            // Cộng dồn để tính tổng số tiền sẽ thanh toán cho all
                            rs.totalPrize += tran.prize;
                            // Nếu không phải robot thì tính tổng tiền đầu ra phải trả cho khách thật
                            if(tran.userId != 0)
                                totalCashOut += tran.prize;
                            tran.refund = tran.betValue - tienDuocTinh;
                            rs.totalRefundXiu += tran.refund;
                            if(tran.userId != 0)
                                totalCashOut += tran.refund;
                            this.updateSumTran(sumTXTMap, tran);
                            this.updateSumTran(sumXiu, tran);
                            this.saveTransactionDetailTX(tran);
                        }
                        catch (Exception e) {
                            Debug.trace((Object)("Error calculate prize user " + tran.username + " error: " + e.getMessage()));
                        }
                    }
                }
                if (potT == null || potT.contributors == null) break;
                for (TransactionTaiXiuDetail tran : potT.contributors) {
                    try {
                        long tienDuocTinh = tran.betValue;
                        if(tran.userId != 0)
                            totalCashIn += tienDuocTinh;
                        if (tongTienTaiDaTinh + tran.betValue > tongTienHopLe) {
                            tienDuocTinh = tongTienHopLe - tongTienTaiDaTinh;
                        }
                        tongTienTaiDaTinh += tienDuocTinh;
                        tran.refund = tran.betValue - tienDuocTinh;
                        rs.totalRefundTai += tran.refund;
                        if(tran.userId != 0)
                            totalCashOut += tran.refund;
                        this.updateSumTran(sumTXTMap, tran);
                        this.updateSumTran(sumTai, tran);
                        this.saveTransactionDetailTX(tran);
                    }
                    catch (Exception e) {
                        Debug.trace((Object)("Error calculate prize user " + tran.username + " error: " + e.getMessage()));
                    }
                }
                break;
            }
            case 1: {
                if (potT != null && potT.contributors != null) {
                    for (TransactionTaiXiuDetail tran : potT.contributors) {
                        try {
                            long tienDuocTinh = tran.betValue;
                            if(tran.userId != 0)
                                totalCashIn += tienDuocTinh;
                            if (tongTienTaiDaTinh + tran.betValue > tongTienHopLe) {
                                tienDuocTinh = tongTienHopLe - tongTienTaiDaTinh;
                            }
                            tongTienTaiDaTinh += tienDuocTinh;
                            tran.prize = (long)((float)tienDuocTinh * (100.0f - this.tax) / 100.0f) + tienDuocTinh;
                            rs.totalPrize += tran.prize;
                            if(tran.userId != 0)
                                totalCashOut += tran.prize;
                            tran.refund = tran.betValue - tienDuocTinh;
                            rs.totalRefundTai += tran.refund;
                            if(tran.userId != 0)
                                totalCashOut += tran.refund;
                            this.updateSumTran(sumTXTMap, tran);
                            this.updateSumTran(sumTai, tran);
                            this.saveTransactionDetailTX(tran);
                        }
                        catch (Exception e) {
                            Debug.trace((Object)("Error calculate prize user " + tran.username + " error: " + e.getMessage()));
                        }
                    }
                }
                if (potX == null || potX.contributors == null) break;
                for (TransactionTaiXiuDetail tran : potX.contributors) {
                    try {
                        long tienDuocTinh = tran.betValue;
                        if(tran.userId != 0)
                            totalCashIn += tienDuocTinh;
                        if (tongTienXiuDaTinh + tran.betValue > tongTienHopLe) {
                            tienDuocTinh = tongTienHopLe - tongTienXiuDaTinh;
                        }
                        tongTienXiuDaTinh += tienDuocTinh;
                        tran.refund = tran.betValue - tienDuocTinh;
                        rs.totalRefundXiu += tran.refund;
                        if(tran.userId != 0)
                            totalCashOut += tran.refund;
                        this.updateSumTran(sumTXTMap, tran);
                        this.updateSumTran(sumXiu, tran);
                        this.saveTransactionDetailTX(tran);
                    }
                    catch (Exception e) {
                        Debug.trace((Object)("Error calculate prize user " + tran.username + " error: " + e.getMessage()));
                    }
                }
                break;
            }
            default: {
                Debug.trace((Object)("Fuck error TX, room=" + this.moneyTypeStr + ", reference= " + referenceId + ", result= " + this.result));
            }
        }
        if (this.moneyType == 1) {
            Debug.trace((Object)("TX phien= " + referenceId + ", tinh toan xong ket qua"));
        }
        rs.totalTai = potT.getTotalValue();
        rs.numBetTai = potT.getNumBet();
        rs.totalXiu = potX.getTotalValue();
        rs.numBetXiu = potX.getNumBet();
        UpdateMoneyTXTask taskTai = new UpdateMoneyTXTask(sumTai);
        taskTai.start();
        if (this.moneyType == 1) {
            Debug.trace((Object)("TX phien= " + referenceId + ", cap nhat xong ben tai"));
        }
        UpdateMoneyTXTask taskXiu = new UpdateMoneyTXTask(sumXiu);
        taskXiu.start();
        if (this.moneyType == 1) {
            Debug.trace((Object)("TX phien= " + referenceId + ", cap nhat xong ben xiu"));
        }
        ArrayList<TransactionTaiXiu> trans = new ArrayList<TransactionTaiXiu>(sumTXTMap.values());
        try {
            this.api.saveResultTaiXiu(rs);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.api.saveTransactionTaiXiu(trans);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.moneyType == 1) {
            CalculateEndTXTask task = new CalculateEndTXTask(trans);
            task.start();

            try {
                HazelcastInstance client = HazelcastClientFactory.getInstance();
                IMap bankMap = client.getMap("txBank2");
                String key = "txBank2:" + this.moneyType;
                long bank = 0L;
                //bankMap.lock(key);
                if (bankMap.containsKey(key)) {
                    bank = (long)bankMap.get(key);
                }
    
                bank += totalCashIn - totalCashOut;
                bankMap.put(key, bank);


                bankMap.put("referenceId", referenceId);
                bankMap.put("totalCashIn", totalCashIn);
                bankMap.put("totalCashOut", totalCashOut);

                //bankMap.unlock(key);
            }
            catch (Exception ex) {
                Debug.trace((Object)(" error resultTX: " + ex.getMessage()));
            }
        }
    }

    private void updateSumTran(Map<String, TransactionTaiXiu> map, TransactionTaiXiuDetail tranDetail) {
        if (map.containsKey(tranDetail.username)) {
            TransactionTaiXiu txt = map.get(tranDetail.username);
            if (txt.betSide == tranDetail.betSide) {
                txt.betValue += tranDetail.betValue;
                txt.totalPrize += tranDetail.prize;
                txt.totalRefund += tranDetail.refund;
                map.put(tranDetail.username, txt);
            }
        } else {
            TransactionTaiXiu tran = new TransactionTaiXiu();
            tran.referenceId = tranDetail.referenceId;
            tran.userId = tranDetail.userId;
            tran.username = tranDetail.username;
            tran.moneyType = tranDetail.moneyType;
            tran.betSide = tranDetail.betSide;
            tran.betValue = tranDetail.betValue;
            tran.totalPrize = tranDetail.prize;
            tran.totalRefund = tranDetail.refund;
            map.put(tranDetail.username, tran);
        }
    }

    public short calculateBalanceTX(int type) {
        long tienDuocTinh;
        long totalPrizeBotTai = 0L;
        long totalPrizeBotXiu = 0L;
        long totalPrizeUserTai = 0L;
        long totalPrizeUserXiu = 0L;
        long tongTienHopLe = this.potTai.getTotalValue() > this.potXiu.getTotalValue() ? this.potXiu.getTotalValue() : this.potTai.getTotalBotBet();
        long tongTienXiuDaTinh = 0L;
        long tongTienTaiDaTinh = 0L;
        for (TransactionTaiXiuDetail tran : this.potXiu.contributors) {
            tienDuocTinh = tran.betValue;
            if (tongTienXiuDaTinh + tran.betValue > tongTienHopLe) {
                tienDuocTinh = tongTienHopLe - tongTienXiuDaTinh;
            }
            tongTienXiuDaTinh += tienDuocTinh;
            if (this.isBot(tran.username)) {
                totalPrizeBotXiu += tienDuocTinh;
                continue;
            }
            totalPrizeUserXiu += tienDuocTinh;
        }
        for (TransactionTaiXiuDetail tran : this.potTai.contributors) {
            tienDuocTinh = tran.betValue;
            if (tongTienTaiDaTinh + tran.betValue > tongTienHopLe) {
                tienDuocTinh = tongTienHopLe - tongTienTaiDaTinh;
            }
            tongTienTaiDaTinh += tienDuocTinh;
            if (this.isBot(tran.username)) {
                totalPrizeBotTai += tienDuocTinh;
                continue;
            }
            totalPrizeUserTai += tienDuocTinh;
        }
        Debug.trace((Object)("Bot tai: " + totalPrizeBotTai + ", bot xiu: " + totalPrizeBotXiu));
        Debug.trace((Object)("User tai: " + totalPrizeUserTai + ", user xiu: " + totalPrizeUserXiu));
        short result = -1;
        switch (type) {
            case 1: {
                result = this.tinhCuaThang(totalPrizeBotTai, totalPrizeBotXiu);
                Debug.trace((Object)("He thong am, force= " + result));
                break;
            }
            case -2: {
                result = this.tinhCuaThang(-this.blackListBetTai, -this.blackListBetXiu);
                Debug.trace((Object)("Black list: " + result));
                break;
            }
            case -3: {
                result = this.tinhCuaThang(this.whiteListBetTai, this.whiteListBetXiu);
                Debug.trace((Object)("White list: " + result));
                break;
            }
            case -1: {
                long totalUserWinSystem = Math.abs(totalPrizeUserTai - totalPrizeUserXiu);
                if (totalUserWinSystem <= this.balance.getMaxWinUser()) {
                    result = this.tinhCuaThang(totalPrizeUserTai, totalPrizeUserXiu);
                    Debug.trace((Object)("Nguoi choi am, force = " + result));
                    break;
                }
                Debug.trace((Object)("Nguoi choi am nhung so tien an qua lo'n= " + totalUserWinSystem));
                result = this.checkHeThongAm(totalPrizeUserTai, totalPrizeUserXiu, totalPrizeBotTai, totalPrizeBotXiu);
                break;
            }
            default: {
                result = this.checkHeThongAm(totalPrizeUserTai, totalPrizeUserXiu, totalPrizeBotTai, totalPrizeBotXiu);
            }
        }
        return result;
    }

    private short tinhCuaThang(long tai, long xiu) {
        if (tai > xiu) {
            return 1;
        }
        if (tai < xiu) {
            return 0;
        }
        return -1;
    }

    private short checkHeThongAm(long totalPrizeUserTai, long totalPrizeUserXiu, long totalPrizeBotTai, long totalPrizeBotXiu) {
        short result = -1;
        long fee = balance.getFee();
        long revenueUser = balance.getRevenueUser();
        long maxUserWin = Math.abs(totalPrizeUserTai - totalPrizeUserXiu);
        if ((float)(revenueUser + (long)((float)maxUserWin * (100.0F - tax) / 100.0F)) >= (float)-fee * ConfigGame.getFloatValue("tx_min_fee", 1.0F)) {
            result = tinhCuaThang(totalPrizeBotTai, totalPrizeBotXiu);
            Debug.trace("Chong he thong am, force= " + result + ", max user win= " + maxUserWin);
        }
        return result;
    }

    public int calculateForceBalance() {
        boolean hasBlackList = this.blackListBetTai + this.blackListBetXiu > 0L;
        boolean hasWhiteList = this.whiteListBetTai + this.whiteListBetXiu > 0L;
        return this.balance.isForceBalance(hasBlackList, hasWhiteList);
    }

    private void saveTransactionDetailTX(TransactionTaiXiuDetail tran) {
        try {
            this.api.saveTransactionTaiXiuDetail(tran);
        }
        catch (Exception e) {
            Debug.trace((Object)("Update transaction detail tai xiu error: " + e.getMessage()));
        }
    }

    public void updateTaiXiuInfo(User user, short remainTimeRutLoc) {
        TaiXiuInfoMsg msg = new TaiXiuInfoMsg();
        msg.gameId = (short)2;
        msg.moneyType = this.moneyType;
        msg.referenceId = this.referenceId;
        msg.remainTime = this.getRemainTime();
        msg.bettingState = this.bettingRound;
        msg.potTai = this.getPotTai();
        msg.potXiu = this.getPotXiu();
        msg.myBetTai = this.getTotalBettingTaiByUsername(user.getName());
        msg.myBetXiu = this.getTotalBettingXiuByUsername(user.getName());
        if (this.resultTX != null) {
            msg.dice1 = (short)this.resultTX.dice1;
            msg.dice2 = (short)this.resultTX.dice2;
            msg.dice3 = (short)this.resultTX.dice3;
        }
        msg.remainTimeRutLoc = remainTimeRutLoc;
        this.sendMessageToUser((BaseMsg)msg, user);
    }

    public boolean isBetting() {
        return this.bettingRound;
    }

    public long getPotTai() {
        return this.potTai.getTotalValue();
    }

    public long getBotBetTai() {
        return this.potTai.getTotalBotBet();
    }

    public long getUserBetTai() {
        return this.potTai.getTotalValue() - this.potTai.getTotalBotBet();
    }

    public long getPotXiu() {
        return this.potXiu.getTotalValue();
    }

    public long getBotBetXiu() {
        return this.potXiu.getTotalBotBet();
    }

    public long getUserBetXiu() {
        return this.potXiu.getTotalValue() - this.potXiu.getTotalBotBet();
    }

    public long getTotalBettingTaiByUsername(String usernname) {
        return this.potTai.getTotalBetByUsername(usernname);
    }

    public long getTotalBettingXiuByUsername(String username) {
        return this.potXiu.getTotalBetByUsername(username);
    }

    public BalanceMoneyTX getBalanceTX() {
        return this.balance;
    }

    public boolean isBot(String username) {
        UserCacheModel model = this.userService.getUser(username);
        return model.isBot();
    }

    public static short getMoneyType(int roomId) {
        return roomId == 0 ? (short)0 : 1;
    }

    public static String getKeyRoom(short moneyType) {
        return "" + moneyType + "_" + 2;
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        if (result) {
            user.setProperty((Object)"MGROOM_TAI_XIU_INFO", (Object)this);
        }
        return result;
    }

    @Override
    public boolean quitRoom(User user) {
        boolean result = super.quitRoom(user);
        if (result) {
            user.removeProperty((Object)"MGROOM_TAI_XIU_INFO");
        }
        return result;
    }

    private final class UpdateMoneyTXTask
    extends Thread {
        private Map<String, TransactionTaiXiu> trans = new HashMap<String, TransactionTaiXiu>();

        private UpdateMoneyTXTask(Map<String, TransactionTaiXiu> trans) {
            this.trans = trans;
        }

        @Override
        public void run() {
            for (Map.Entry<String, TransactionTaiXiu> entry : this.trans.entrySet()) {
                try {
                    String username = entry.getKey();
                    TransactionTaiXiu txt = entry.getValue();
                    long currentMoney = MGRoomTaiXiu.this.userService.getCurrentMoneyUserCache(username, MGRoomTaiXiu.this.moneyTypeStr);
                    if (txt.totalPrize == 0L && txt.totalRefund == 0L) {
                        MGRoomTaiXiu.this.userService.updateMoney(username, 0L, MGRoomTaiXiu.this.moneyTypeStr, "OverUnder", "", "", 0L, Long.valueOf(MGRoomTaiXiu.this.referenceId), TransType.END_TRANS);
                    } else {
                        MoneyResponse res;
                        if (txt.totalPrize > 0L) {
                            TransType transType = TransType.END_TRANS;
                            if (txt.totalRefund > 0L) {
                                transType = TransType.IN_TRANS;
                            }
                            long fee = (long)(MGRoomTaiXiu.this.tax * (float)txt.totalPrize / (200.0f - MGRoomTaiXiu.this.tax));
                            MoneyResponse res2 = MGRoomTaiXiu.this.userService.updateMoney(username, txt.totalPrize, MGRoomTaiXiu.this.moneyTypeStr, "OverUnder", "Th\u1eafng t\u00e0i x\u1ec9u", "Phi\u00ean " + MGRoomTaiXiu.this.referenceId, fee, Long.valueOf(MGRoomTaiXiu.this.referenceId), transType);
                            if (res2.isSuccess()) {
                                if (MGRoomTaiXiu.this.moneyType == 1 && !MGRoomTaiXiu.this.isBot(username)) {
                                    MGRoomTaiXiu.this.balance.addWin(txt.totalPrize);
                                    MGRoomTaiXiu.this.balance.addFee(fee);
                                }
                                currentMoney = res2.getCurrentMoney();
                                long totalExchange = (long)((float)txt.totalPrize * (100.0f - MGRoomTaiXiu.this.tax) / (200.0f - MGRoomTaiXiu.this.tax));
                                if (MGRoomTaiXiu.this.moneyType == 1 && totalExchange >= (long)BroadcastMessageServiceImpl.MIN_MONEY) {
                                    MGRoomTaiXiu.this.broadcastMsgService.putMessage(Games.TAI_XIU.getId(), username, totalExchange);
                                }
                            }
                        }
                        if (txt.totalRefund > 0L && (res = MGRoomTaiXiu.this.userService.updateMoney(username, txt.totalRefund, MGRoomTaiXiu.this.moneyTypeStr, "OverUnder", "Ho\u00e0n tr\u1ea3 t\u00e0i x\u1ec9u", "Phi\u00ean " + MGRoomTaiXiu.this.referenceId, 0L, Long.valueOf(MGRoomTaiXiu.this.referenceId), TransType.END_TRANS)).isSuccess()) {
                            if (MGRoomTaiXiu.this.moneyType == 1 && !MGRoomTaiXiu.this.isBot(username)) {
                                MGRoomTaiXiu.this.balance.addWin(txt.totalRefund);
                            }
                            currentMoney = res.getCurrentMoney();
                        }
                    }
                    UpdatePrizeTaiXiuMsg msg = new UpdatePrizeTaiXiuMsg();
                    msg.moneyType = MGRoomTaiXiu.this.moneyType;
                    msg.totalMoney = txt.totalPrize + txt.totalRefund;
                    msg.currentMoney = currentMoney;
                    MGRoomTaiXiu.this.sendMessageToUser((BaseMsg)msg, username);
                }
                catch (Exception e) {
                    Debug.trace((Object)("Update tai xiu money phien " + MGRoomTaiXiu.this.referenceId + " error: " + e.getMessage()));
                }
            }
        }
    }

    private final class CalculateEndTXTask
    extends Thread {
        private List<TransactionTaiXiu> trans;

        private CalculateEndTXTask(List<TransactionTaiXiu> trans) {
            this.trans = trans;
        }

        @Override
        public void run() {
            try {
                MGRoomTaiXiu.this.api.calculateThanhDu(MGRoomTaiXiu.this.referenceId, this.trans, (int)MGRoomTaiXiu.this.result);
                for (TransactionTaiXiu tran : this.trans) {
                    if (tran.betValue - tran.totalRefund < 20000L) continue;
                    int soLuotThem = RutLocUtils.getLuotRutLoc(tran.betValue - tran.totalRefund);
                    int soLuotRut = MGRoomTaiXiu.this.api.updateLuotRutLoc(tran.username, soLuotThem);
                    UpdateRutLocMsg msg = new UpdateRutLocMsg();
                    msg.soLuotRut = soLuotRut;
                    MGRoomTaiXiu.this.sendMessageToUser((BaseMsg)msg, tran.username);
                }
            }
            catch (Exception e) {
                Debug.trace((Object)("Error save tai xiu: " + e.getMessage()));
            }
        }
    }

}

