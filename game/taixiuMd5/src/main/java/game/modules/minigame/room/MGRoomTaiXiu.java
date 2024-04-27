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
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.dal.entities.taixiu.ResultTaiXiu;
import com.vinplay.dal.entities.taixiu.ResultTaiXiuMd5;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.TaiXiuService;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuMd5ServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.utils.TelegramAlert;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.response.minigame.TaiXiuAdmin;
import com.vinplay.vbee.common.statics.TransType;
import game.modules.minigame.TaiXiuModule;
import game.modules.minigame.cmd.rev.BetTaiXiuCmd;
import game.modules.minigame.cmd.send.*;
import game.modules.minigame.entities.BalanceMoneyTX;
import game.modules.minigame.entities.MinigameConstant;
import game.modules.minigame.entities.PotTaiXiu;
import game.modules.minigame.utils.TaiXiuUtils;
import game.utils.ConfigGame;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// todo : con của MGRoom
public class MGRoomTaiXiu extends MGRoom {
    private static final double PHAN_TRAM_TIEN_HU = 0.005;
    public long referenceId;
    private short moneyType;
    private String moneyTypeStr;
    private PotTaiXiu potTai;
    private PotTaiXiu potXiu;
    private long startTime = 0L;
    private short result = (short) -1;
    public boolean bettingRound = false;
    public boolean enableBetting = false;
    public ResultTaiXiuMd5 resultTX;
    private TaiXiuService taiXiuService = new TaiXiuMd5ServiceImpl(); // tài xỉu service lấy cả trong rabbitmq và cả trong cache server
    private UserService userService = new UserServiceImpl();   // user service
    private CacheService cacheService = new CacheServiceImpl(); // cache service
    private BroadcastMessageService broadcastMsgService = new BroadcastMessageServiceImpl();
    private float tax = MinigameConstant.MINIGAME_TAX_VIN;
    private BalanceMoneyTX balance = new BalanceMoneyTX();  //
    private long blackListBetTai = 0L;
    private long blackListBetXiu = 0L;
    private long whiteListBetTai = 0L;
    private long whiteListBetXiu = 0L;
    public static long soTienNguoiChoiDatTai = 0L;
    public static long soTienNguoiChoiDatXiu = 0L;

    private static final org.apache.log4j.Logger logger = Logger.getLogger((String) "recharge");

    TaiXiuModule module;

    public MGRoomTaiXiu(String name, long referenceId, short moneyType, TaiXiuModule module) {
        super(name);
        this.module = module;
        this.moneyType = moneyType;
        this.moneyTypeStr = "xu";
        if (moneyType == 1) {
            this.moneyTypeStr = "vin";
            this.tax = MinigameConstant.MINIGAME_TAX_VIN;
            ReportMoneySystemModel model = this.taiXiuService.getReportTX(ConfigGame.getIntValue("interval_reset_balance", 10));
            if (model != null) {
                this.balance = new BalanceMoneyTX(model.moneyWin, model.moneyLost, model.fee, model.dateReset);
                Debug.trace((Object) ("TAI XIU VIN, win=" + model.moneyWin + ", loss=" + model.moneyLost + ", fee= " + model.fee + ", date reset= " + model.dateReset));
            }
        } else {
            this.tax = MinigameConstant.MINIGAME_TAX_XU;
        }
        this.potTai = new PotTaiXiu();
        this.potXiu = new PotTaiXiu();
        this.referenceId = referenceId;
    }

    // todo : bắt đầu một game mới truyền vào tham số là referenceID
    public void startNewGame(long newReferenceId) {
        this.referenceId = newReferenceId;
        this.bettingRound = true;
        this.enableBetting = true;
        this.blackListBetTai = 0L;
        this.blackListBetXiu = 0L;
        this.whiteListBetTai = 0L;
        this.whiteListBetXiu = 0L;
        soTienNguoiChoiDatTai = 0L;
        soTienNguoiChoiDatXiu = 0L;
        this.potTai.renew();
        this.potXiu.renew();
        this.startTime = System.currentTimeMillis();
        Debug.trace((Object) ("START NEW ROUND " + this.referenceId));
    }

    // todo :kết thúc 1 game
    public void finish() {
        this.startTime = System.currentTimeMillis();
        this.bettingRound = false;
        try {
            this.cacheService.removeKey("md5_allow_betting_" + this.referenceId); // xóa key cho phép đặt tài xỉu
            this.cacheService.removeKey("md5_force_result_" + this.referenceId); // xóa key kết quả bắt buộc
        } catch (Exception e) {

        }

    }

    // todo : không cho phép đặt cược
    public void disableBetting() {
        this.enableBetting = false;
        this.cacheService.setValue("md5_allow_betting_" + this.referenceId, 0); // không cho phép đặt cược nữa
    }

    public void updateResultDices(short[] dices, short result) {
        this.result = result;
        UpdateResultDicesMsg msg = new UpdateResultDicesMsg();
        msg.result = result;
        msg.dice1 = dices[0];
        msg.dice2 = dices[1];
        msg.dice3 = dices[2];
        this.resultTX.referenceId = this.referenceId;
        this.resultTX.dice1 = msg.dice1;
        this.resultTX.dice2 = msg.dice2;
        this.resultTX.dice3 = msg.dice3;
        this.resultTX.result = msg.result;
        this.resultTX.moneyType = this.moneyType;
        this.sendMessageToRoom(msg);
    }

    // todo : lấy thời gian còn lại bằng cách lấy thời gian hiện tại trừ đi thời gian bắt đầu
    public short getRemainTime() {
        long currentTime = System.currentTimeMillis();
        int remainTime = (int) ((currentTime - this.startTime) / 1000L);
        if (remainTime < 0) {
            remainTime = 0;
        } else if (remainTime > 25) {
            remainTime = 25;
        }
        if (this.bettingRound) {
            return (short) (25 - this.module.count);
        }
        return (short) (35 - this.module.count);
    }

    // todo : bet tài xỉu
    public void betTaiXiu(User user, BetTaiXiuCmd cmd) {
        BetTaiXiuMsg msg = this.betTaiXiu(user.getName(), cmd.userId, cmd.betValue, cmd.inputTime, cmd.moneyType, cmd.betSide, false);
        this.sendMessageToUser((BaseMsg) msg, user); // todo : gửi message về client
        TelegramAlert.SendMessageBetTXMD5(user.getName(), cmd.betValue, cmd.betSide);

    }

    // todo : đặt tài xỉu
    public BetTaiXiuMsg betTaiXiu(String nickname, int userId, long betValue, short inputTime, short moneyType, short betSide, boolean isBot) {
        long currentMoney = 0L;
        int result = 2;
        if (this.enableBetting) {
            if (betValue >= 100L) {

                if (!isBot && moneyType == 1) {
                    if (betSide == 1) {
                        soTienNguoiChoiDatTai += betValue; // người chơi đặt tài
                    } else {
                        soTienNguoiChoiDatXiu += betValue; // người đặt xỉu
                    }
                }
                inputTime = this.getRemainTime(); // lấy thời gian còn lại
                currentMoney = this.userService.getMoneyUserCache(nickname, this.moneyTypeStr);  // lấy số tiền hiện tại của user dựa trên nick name

                if (betValue > currentMoney) {
                    result = 3;
                } else {
                    TransactionTaiXiuDetail transTX = new TransactionTaiXiuDetail(this.referenceId, userId, nickname, betValue, (int) betSide, (int) inputTime, (int) moneyType);
                    if (betSide == 1 && this.potXiu.getTotalBetByUsername(nickname) > 0L || betSide == 0 && this.potTai.getTotalBetByUsername(nickname) > 0L) {
                        result = 5;
                    } else {
                        String betSideStr = betSide == 0 ? "X\u1ec9u" : "T\u00e0i";

                        MoneyResponse res = new MoneyResponse(false, "1001");
                        if (!isBot) { // trừ tiền đặt cược
                            res = this.userService.updateMoney(nickname, -betValue, this.moneyTypeStr, Games.TAI_XIU_MD5.getName(), "T\u00e0i x\u1ec9u: \u0110\u1eb7t c\u01b0\u1ee3c", "Phii\u00ean " + this.referenceId + ": \u0111\u1eb7t " + betSideStr + " (" + inputTime + ")", 0L, Long.valueOf(this.referenceId), TransType.START_TRANS);

                        } else {
                            res.setSuccess(true);
                        }
                        if (res.isSuccess()) {
                            if (!this.enableBetting) { // kiểm tra xem có phải đang trong quá trình đặt cược hay ko , nếu ko
                                result = 1;
                                if (!isBot) { // hoàn trả tiền cược
                                    this.userService.updateMoney(nickname, betValue, this.moneyTypeStr, Games.TAI_XIU_MD5.getName(), "T\u00e0i x\u1ec9u: Tr\u1ea3 c\u01b0\u1ee3c", "Ho\u00e0n tr\u1ea3 \u0111\u1eb7t c\u01b0\u1ee3c phi\u00ean " + this.referenceId, 0L, Long.valueOf(this.referenceId), TransType.END_TRANS);

                                }
                            } else { // nếu đang trong quá trình đặt cược
                                isBot = this.isBot(nickname);
                                if (moneyType == 1 && !isBot) {
                                    this.balance.addBet(betValue);
                                    if (betValue >= (long) ConfigGame.getIntValue("tx_min_money_black_list", 2000000) && ConfigGame.inBlackList(nickname) && new Random().nextInt(100) <= ConfigGame.getIntValue("tx_black_list_percent", 50)) {
                                        Debug.trace((Object) ("Black list " + nickname + " money= " + betValue + ", bet side= " + betSide));
                                        if (betSide == 1) {
                                            this.blackListBetTai += betValue;
                                        } else {
                                            this.blackListBetXiu += betValue;
                                        }
                                    }
                                    if (betValue >= (long) ConfigGame.getIntValue("tx_min_money_white_list", 2000000) && ConfigGame.inWhiteList(nickname) && new Random().nextInt(100) <= ConfigGame.getIntValue("tx_white_list_percent", 50)) {
                                        Debug.trace((Object) ("White list " + nickname + " money= " + betValue + ", bet side= " + betSide));
                                        if (betSide == 1) {
                                            this.whiteListBetTai += betValue;
                                        } else {
                                            this.whiteListBetXiu += betValue;
                                        }
                                    }
                                }
                                if (betSide == 1) {
                                    this.potTai.bet(transTX, isBot); // todo : update tiền bên cửa tài
                                } else {
                                    this.potXiu.bet(transTX, isBot); // todo : update tiền bên cửa xỉu
                                }
                                currentMoney = res.getCurrentMoney();
                                transTX.genTransactionCode();
                                if (!isBot) {
                                    TaiXiuUtils.logBetTaiXiu(transTX); // log bet tài xỉu
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
        msg.Error = (byte) result;
        msg.currentMoney = currentMoney; // số tiền hiện tại

        return msg;
    }

    public void updateTaiXiuPerSecond(int amountBotTaiFake, int amountBotXiuFake, int secondGamePlay) {
        UpdateTaiXiuPerSecondMsg msg = new UpdateTaiXiuPerSecondMsg();
        msg.remainTime = this.getRemainTime();
        msg.bettingState = this.bettingRound;
        msg.potTai = this.getPotTai();
        msg.potXiu = this.getPotXiu();

        msg.numBetTai = (this.potTai.getNumBet() + amountBotTaiFake);
        msg.numBetXiu = (this.potXiu.getNumBet() + amountBotXiuFake);
        msg.moneyHu = TaiXiuModule.moneyHu;
        msg.md5TextResult = resultTX.getMd5TextResult();
        if (secondGamePlay >= 26) {
            msg.plaintTextResult = resultTX.getPlantTextResult();
        }
        cacheService.setValue("Md5_Lobby_tx_tai_" + this.moneyType, String.valueOf(this.getPotTai()));
        cacheService.setValue("Md5_Lobby_tx_xiu_" + this.moneyType, String.valueOf(this.getPotXiu()));
        this.sendMessageToRoom(msg);

//        System.out.println("Second: " + secondGamePlay + " | md5: " + resultTX.getMd5TextResult() + " | plain: " + resultTX.getPlantTextResult());
    }

    // todo : tính toán kết quả
    public void calculatePrize(long referenceId) {
        PotTaiXiu potX = this.potXiu;
        PotTaiXiu potT = this.potTai;
        HashMap<String, TransactionTaiXiu> sumTXTMap = new HashMap<String, TransactionTaiXiu>();
        HashMap<String, TransactionTaiXiu> sumTai = new HashMap<String, TransactionTaiXiu>();
        HashMap<String, TransactionTaiXiu> sumXiu = new HashMap<String, TransactionTaiXiu>();

        long totalCashIn = 0L;
        long totalCashOut = 0L;
        long totalDice = this.resultTX.dice1 + this.resultTX.dice2 + this.resultTX.dice3;
        ResultTaiXiu rs = this.resultTX;
        Debug.trace("resultTX {}", this.resultTX);

        // Tính toán tiền thắng thua trong game
        switch (this.result) {
            case 0: {
                if (potX != null && potX.contributors != null) {
                    for (TransactionTaiXiuDetail tran : potX.contributors) {
                        try {
                            // Nếu không phải robot thì tính tổng tiền đầu vào của khách thật
                            if (tran.userId != 0) {
                                totalCashIn += tran.betValue;
                            }

                            // giải thưởng, hay nói cách khác là số tiền thắng
                            tran.prize = Math.round((long) ((float) tran.betValue * (100.0f - this.tax) / 100.0f) + tran.betValue);

                            // Cộng dồn để tính tổng số tiền trả lại
                            rs.totalPrize += tran.prize;

                            // Nếu không phải robot thì tính tổng tiền đầu ra phải trả cho khách thật
                            if (tran.userId != 0) {
                                totalCashOut += tran.prize;
                            }

                            // Tổng tiền lỗ lãi
                            this.updateSumTran(sumTXTMap, tran);
                            this.updateSumTran(sumXiu, tran);

                            // Update giao dịch
                            this.saveTransactionDetailTX(tran);
                            TaiXiuModule.fundTxMD5 -= tran.refund;
                        } catch (Exception e) {
                            Debug.trace((Object) ("Error calculate prize user " + tran.username + " error: " + e.getMessage()));
                        }
                    }
                }
                if (potT == null || potT.contributors == null) {
                    break;
                }
                for (TransactionTaiXiuDetail tran : potT.contributors) {
                    try {
                        if (tran.userId != 0) {
                            totalCashIn += tran.betValue;
                        }

                        // Tổng tiền lỗ lãi
                        this.updateSumTran(sumTXTMap, tran);
                        this.updateSumTran(sumTai, tran);

                        // Update giao dịch
                        this.saveTransactionDetailTX(tran);
                    } catch (Exception e) {
                        Debug.trace((Object) ("Error calculate prize user " + tran.username + " error: " + e.getMessage()));
                    }
                }
                break;
            }
            case 1: {
                if (potT != null && potT.contributors != null) {
                    for (TransactionTaiXiuDetail tran : potT.contributors) {
                        try {
                            // không tính toán với
                            if (tran.userId != 0) {
                                totalCashIn += tran.betValue;
                            }

                            tran.prize = Math.round((long) ((float) tran.betValue * (100.0f - this.tax) / 100.0f) + tran.betValue);
                            rs.totalPrize += tran.prize;
                            if (tran.userId != 0) {
                                totalCashOut += tran.prize;
                            }

                            // Tổng tiền lỗ lãi
                            this.updateSumTran(sumTXTMap, tran);
                            this.updateSumTran(sumTai, tran);

                            // Update giao dịch
                            this.saveTransactionDetailTX(tran);
                            TaiXiuModule.fundTxMD5 -= tran.refund;
                        } catch (Exception e) {
                            Debug.trace((Object) ("Error calculate prize user " + tran.username + " error: " + e.getMessage()));
                        }
                    }
                }
                if (potX == null || potX.contributors == null) break;
                for (TransactionTaiXiuDetail tran : potX.contributors) {
                    try {
                        if (tran.userId != 0) {
                            totalCashIn += tran.betValue;
                        }

                        // Tổng tiền lỗ lãi
                        this.updateSumTran(sumTXTMap, tran);
                        this.updateSumTran(sumXiu, tran);

                        // Update giao dịch
                        this.saveTransactionDetailTX(tran);
                    } catch (Exception e) {
                        Debug.trace((Object) ("Error calculate prize user " + tran.username + " error: " + e.getMessage()));
                    }
                }
                break;
            }
            default: {
                Debug.trace((Object) ("Fuck error TX, room=" + this.moneyTypeStr + ", reference= " + referenceId + ", result= " + this.result));
            }
        }

        ArrayList<TransactionTaiXiu> trans = new ArrayList<TransactionTaiXiu>(sumTXTMap.values());
        if (this.moneyType == 1) {
            logger.trace("TX phien= " + referenceId + ", tinh toan xong ket qua");

//            //Xử lý khi nổ hũ
            if (totalDice == 3 || totalDice == 18) {
                rs.statusHu = 0;
            }

            try {
                HazelcastInstance client = HazelcastClientFactory.getInstance();
                IMap bankMap = client.getMap("txBank_md5");  // Đây có thể là quỹ thưởng được sử dụng để tính toán cân bằng lỗ lãi của nhà cái khi ra kết quả
                String key = "txBank:" + this.moneyType;
                long bank = 0L;
                if (bankMap.containsKey(key)) {
                    bank = (long) bankMap.get(key);
                }
                logger.error("calculatePrize key:" + key + " bank:" + bank);

                bank += totalCashIn - totalCashOut;
                bankMap.put(key, bank);
                logger.error("calculatePrize 2 key:" + key + " bank:" + bank);

                bankMap.put("referenceId", referenceId);
                bankMap.put("totalCashIn", totalCashIn);
                bankMap.put("totalCashOut", totalCashOut);
            } catch (Exception ex) {
                logger.error("calculatePrize ex:" + ex.getMessage());
                Debug.trace((Object) (" error resultTX: " + ex.getMessage()));
            }
        }

        rs.totalTai = potT.getTotalValue();
        rs.numBetTai = potT.getNumBet();
        rs.totalXiu = potX.getTotalValue();
        rs.numBetXiu = potX.getNumBet();
        rs.moneyHu = TaiXiuModule.moneyHu;
        rs.moneyType = this.moneyType;

        // Tính tiền và trả lại cho khách
        new UpdateMoneyTXTask(sumTai).start();
        if (this.moneyType == 1) {
            Debug.trace((Object) ("TX phien= " + referenceId + ", cap nhat xong ben tai"));
        }

        new UpdateMoneyTXTask(sumXiu).start();
        if (this.moneyType == 1) {
            Debug.trace((Object) ("TX phien= " + referenceId + ", cap nhat xong ben xiu"));
        }

        try {
            // lưu kết quả tài xỉu
            Debug.trace((Object) ("Ket qua của phiên sẽ lưu "));
            this.taiXiuService.saveResultTaiXiu(rs);
        } catch (Exception e) {
            ExceptionUtils.printRootCauseStackTrace(e);
        }

        try {
            // Save tổng giao dịch trên phiên
            if (this.taiXiuService.saveTransactionTaiXiu(trans)) {
                Debug.info("Save thanh cong");
            }
        } catch (Exception e) {
            logger.error("calculatePrize ex:" + e.getMessage());
        }
    }

    private void updateSumTran(Map<String, TransactionTaiXiu> map, TransactionTaiXiuDetail tranDetail) {
        if (tranDetail.username.equals("banhday"))
            System.out.println(tranDetail.prize + "tien truoc khi nha lại");
        if (map.containsKey(tranDetail.username)) {
            TransactionTaiXiu txt = map.get(tranDetail.username);
            if (txt.betSide == tranDetail.betSide) {
                txt.betValue += tranDetail.betValue;
                txt.totalPrize += tranDetail.prize;
                txt.totalRefund += tranDetail.refund;
                //txt.totalExchange += tranDetail.totalExchange;
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
            //tran.totalExchange = tranDetail.totalExchange;
            map.put(tranDetail.username, tran);
        }
    }

    /**
     * Lưu transaction một người dùng mỗi phiên
     *
     * @param tran
     */
    private void saveTransactionDetailTX(TransactionTaiXiuDetail tran) {
        try {
            this.taiXiuService.saveTransactionTaiXiuDetail(tran);
        } catch (Exception e) {
            Debug.trace((Object) ("Update transaction detail tai xiu error: " + e.getMessage()));
        }
    }

    public void updateTaiXiuInfo(User user, short remainTimeRutLoc) {
        TaiXiuInfoMsg msg = new TaiXiuInfoMsg();
        msg.gameId = (short) 2;
        msg.moneyType = this.moneyType;
        msg.referenceId = this.referenceId;
        msg.remainTime = this.getRemainTime();
        msg.bettingState = this.bettingRound;
        msg.potTai = this.getPotTai();
        msg.potXiu = this.getPotXiu();
        msg.myBetTai = this.getTotalBettingTaiByUsername(user.getName());
        msg.myBetXiu = this.getTotalBettingXiuByUsername(user.getName());
        msg.moneyHu = TaiXiuModule.moneyHu;
        if (this.resultTX != null) {
            msg.dice1 = (short) this.resultTX.dice1;
            msg.dice2 = (short) this.resultTX.dice2;
            msg.dice3 = (short) this.resultTX.dice3;
        }
        msg.remainTimeRutLoc = remainTimeRutLoc;
        this.sendMessageToUser((BaseMsg) msg, user);
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

    public List<TaiXiuAdmin> getListTransaction() {
        return Stream.of(this.potTai.getContributorNotBot(), this.potXiu.getContributorNotBot()).flatMap(Collection::stream).collect(Collectors.toList());
    }


    public long getTotalMoneyTai() {
        return this.potTai.getTotalValue();
    }

    public long getTotalMoneyXiu() {
        return this.potXiu.getTotalValue();
    }


    public long getNumberUserRealTai() {
        return this.potTai.getNumberUserNotBot();
    }

    public long getNumberUserRealXiu() {
        return this.potXiu.getNumberUserNotBot();
    }

    public long getNumberUerAndBotTai() {
        return this.potTai.getNumberUserBot();
    }

    public long getNumberUerAndBotXiu() {
        return this.potXiu.getNumberUserBot();
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

    public long getTotalBettingTaiByUsername(String username) {
        return this.potTai.getTotalBetByUsername(username);
    }

    public long getTotalBettingXiuByUsername(String username) {
        return this.potXiu.getTotalBetByUsername(username);
    }

    public boolean isBot(String username) {
        UserCacheModel model = this.userService.getUser(username);
        return model.isBot();
    }

    public static short getMoneyType(int roomId) {
        return roomId == 0 ? (short) 0 : 1;
    }

    public static String getKeyRoom(short moneyType) {
        return "" + moneyType + "_" + 2;
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        if (result) {
            user.setProperty((Object) "MGROOM_TAI_XIU_INFO", (Object) this);
        }
        return result;
    }

    @Override
    public boolean quitRoom(User user) {
        boolean result = super.quitRoom(user);
        if (result) {
            user.removeProperty((Object) "MGROOM_TAI_XIU_INFO");
        }
        return result;
    }

    @Override
    protected void checkResetPot() {
        //todo : check reset pot
    }

    private final class UpdateMoneyTXTask extends Thread {
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
                        MGRoomTaiXiu.this.userService.updateMoney(username, 0L, MGRoomTaiXiu.this.moneyTypeStr, "TaiXiuMd5", "", "", 0L, MGRoomTaiXiu.this.referenceId, TransType.END_TRANS);
                    } else {
                        MoneyResponse res;
                        if (txt.totalPrize > 0L) {
                            TransType transType = TransType.END_TRANS;
                            if (txt.totalRefund > 0L) {
                                transType = TransType.IN_TRANS;
                            }
                            long fee = Math.round((long) (MGRoomTaiXiu.this.tax * (float) txt.totalPrize / (200.0f - MGRoomTaiXiu.this.tax)));
                            MoneyResponse res2 = new MoneyResponse(false, "1001");
                            if (!MGRoomTaiXiu.this.isBot(username)) {
                                if (username.equals("banhday"))
                                    System.out.println(txt.totalPrize + " qua tien nay ghet vl");
                                res2 = MGRoomTaiXiu.this.userService.updateMoney(username, txt.totalPrize, MGRoomTaiXiu.this.moneyTypeStr, "TaiXiuMd5", "Th\u1eafng t\u00e0i x\u1ec9u", "Phi\u00ean " + MGRoomTaiXiu.this.referenceId, fee, MGRoomTaiXiu.this.referenceId, transType);
                            } else {
                                res2.setSuccess(true);
                            }
                            if (res2.isSuccess()) {
                                if (MGRoomTaiXiu.this.moneyType == 1 && !MGRoomTaiXiu.this.isBot(username)) {
                                    MGRoomTaiXiu.this.balance.addWin(txt.totalPrize);
                                    MGRoomTaiXiu.this.balance.addFee(fee);
                                }
                                currentMoney = res2.getCurrentMoney();
                                long totalExchange = Math.round((long) ((float) txt.totalPrize * (100.0f - MGRoomTaiXiu.this.tax) / (200.0f - MGRoomTaiXiu.this.tax)));
                                if (MGRoomTaiXiu.this.moneyType == 1 && totalExchange >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                                    MGRoomTaiXiu.this.broadcastMsgService.putMessage(Games.TAI_XIU.getId(), username, totalExchange);
                                }
                            }
                        }
                        if (txt.totalRefund > 0L) {
                            if (!MGRoomTaiXiu.this.isBot(username)) {
                                res = MGRoomTaiXiu.this.userService.updateMoney(username, txt.totalRefund, MGRoomTaiXiu.this.moneyTypeStr, "TaiXiuMd5", "Ho\u00e0n tr\u1ea3 t\u00e0i x\u1ec9u", "Phi\u00ean " + MGRoomTaiXiu.this.referenceId, 0L, Long.valueOf(MGRoomTaiXiu.this.referenceId), TransType.END_TRANS);
                                if (res.isSuccess()) {
                                    if (MGRoomTaiXiu.this.moneyType == 1) {
                                        MGRoomTaiXiu.this.balance.addWin(txt.totalRefund);
                                    }
                                    currentMoney = res.getCurrentMoney();
                                }
                            }
                        }
                    }
                    UpdatePrizeTaiXiuMsg msg = new UpdatePrizeTaiXiuMsg();
                    msg.moneyType = MGRoomTaiXiu.this.moneyType;
                    msg.totalMoney = txt.totalPrize + txt.totalRefund;
                    msg.currentMoney = currentMoney;
                    msg.moneyHu = TaiXiuModule.moneyHu;
                    MGRoomTaiXiu.this.sendMessageToUser((BaseMsg) msg, username);
                } catch (Exception e) {
                    Debug.trace((Object) ("Update tai xiu money phien " + MGRoomTaiXiu.this.referenceId + " error: " + e.getMessage()));
                }
            }
        }
    }
}

