
package game.modules.minigame.room;

import bitzero.server.entities.User;
import bitzero.util.common.business.Debug;
import com.vinplay.dal.entities.taixiu.ResultTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.TaiXiuService;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuKubetServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.messages.BetTXMD5Message;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.response.minigame.TaiXiuAdmin;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.statics.TransType;
import game.modules.minigame.TaiXiuModule;
import game.modules.minigame.cmd.rev.BetTaiXiuCmd;
import game.modules.minigame.cmd.send.*;
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
    // mã phiên
    public long referenceId;
    // kiểu tiền xu/vin
    private final short moneyType;
    // kiểu tiền dạng chữ
    private String moneyTypeStr;
    // đối tượng thống kê cửa tài
    private final PotTaiXiu potTai;
    // đối tượng thống  kê cửa xỉu
    private final PotTaiXiu potXiu;
    // kết quả: 1-Tài, 0-Xỉu
    private final PotTaiXiu potChan;
    // đối tượng thống  kê cửa xỉu
    private final PotTaiXiu potLe;
    private short result = (short) -1;
    // trạng thái có được đặt cược hay ko
    public boolean bettingRound = false;
    // trạng thái có được đặt cược hay ko
    public boolean enableBetting = false;
    // đối tượng đại diện cho kết quả của phiên hiện tại
    public ResultTaiXiu resultTX;
    // dịch vụ giao tiếp với DB
    private final TaiXiuService taiXiuService = new TaiXiuKubetServiceImpl(); // tài xỉu service lấy cả trong rabbitmq và cả trong cache server
    private final UserService userService = new UserServiceImpl();   // user service
    private final CacheService cacheService = new CacheServiceImpl(); // cache service
    private final BroadcastMessageService broadcastMsgService = new BroadcastMessageServiceImpl();
    private final float tax;

    private static final org.apache.log4j.Logger logger = Logger.getLogger("recharge");

    private final TaiXiuModule module;

    public MGRoomTaiXiu(String name, long referenceId, short moneyType, TaiXiuModule module) {
        super(name);
        this.module = module;
        this.moneyType = moneyType;
        this.moneyTypeStr = "xu";
        if (moneyType == 1) {
            this.moneyTypeStr = "vin";
            this.tax = MinigameConstant.MINIGAME_TAX_VIN;
        } else {
            this.tax = MinigameConstant.MINIGAME_TAX_XU;
        }
        this.potTai = new PotTaiXiu();
        this.potXiu = new PotTaiXiu();
        this.potChan = new PotTaiXiu();
        this.potLe = new PotTaiXiu();
        this.referenceId = referenceId;
    }

    // todo : bắt đầu một game mới truyền vào tham số là referenceID
    public void startNewGame(long newReferenceId) {
        this.referenceId = newReferenceId;
        this.bettingRound = true;
        this.enableBetting = true;
        this.potTai.renew();
        this.potXiu.renew();
        this.potChan.renew();
        this.potLe.renew();
        Debug.trace("START NEW ROUND " + this.referenceId);
    }

    // todo :kết thúc 1 game
    public void finish() {
        this.bettingRound = false;
        try {
            this.cacheService.removeKey("kubet_allow_betting_" + this.referenceId); // xóa key cho phép đặt tài xỉu
            this.cacheService.removeKey("kubet_force_result_" + this.referenceId); // xóa key kết quả bắt buộc
        } catch (Exception ignored) {
        }
    }

    // todo : không cho phép đặt cược
    public void disableBetting() {
        this.enableBetting = false;
        this.cacheService.setValue("kubet_allow_betting_" + this.referenceId, 0); // không cho phép đặt cược nữa
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
//        this.sendMessageToRoom(msg);
    }

    // todo : lấy thời gian còn lại bằng cách lấy thời gian hiện tại trừ đi thời gian bắt đầu
    public short getRemainTime() {
//        if (this.bettingRound) {
//            return (short) (50 - this.module.count);
//        }
//        return (short) (65 - this.module.count);
        return (short) this.module.count;
    }

    // todo : bet tài xỉu
    public void betTaiXiu(User user, BetTaiXiuCmd cmd) {
        BetTaiXiuMsg msg = this.betTaiXiu(user.getName(), user.getId(), cmd.betValue, cmd.inputTime, cmd.moneyType, cmd.betSide, false, referenceId);
        this.sendMessageToUser(msg, user); // todo : gửi message về client
    }

    // todo : đặt tài xỉu
    public BetTaiXiuMsg betTaiXiu(String nickname, int userId, long betValue, short inputTime, short moneyType, short betSide, boolean isBot, long referenceId) {
        long currentMoney = 0L;
        int result = 2;
        if (this.enableBetting) {
            if (betValue >= 100L) {
                inputTime = this.getRemainTime(); // lấy thời gian còn lại
                currentMoney = this.userService.getMoneyUserCache(nickname, this.moneyTypeStr);  // lấy số tiền hiện tại của user dựa trên nick name

                if (betValue > currentMoney) {
                    result = 3;
                } else {
                    TransactionTaiXiuDetail transTX = new TransactionTaiXiuDetail(this.referenceId, userId, nickname, betValue, betSide, inputTime, moneyType, module.kubetSessionId);
                    String betSideStr = betSide == 0 ? "Xỉu" : "Tài";
                    switch (betSide) {
                        case 0:
                            betSideStr = "xỉu";
                            break;
                        case 1:
                            betSideStr = "tài";
                            break;
                        case 2:
                            betSideStr = "chẵn";
                            break;
                        case 3:
                            betSideStr = "lẻ";
                            break;
                    }

                    MoneyResponse res = new MoneyResponse(false, "1001");
                    if (!isBot) { // trừ tiền đặt cược
                        res = this.userService.updateMoney(nickname, -betValue, this.moneyTypeStr, Games.TAI_XIU_KUBET.getName(), "T.Xỉu KUBET: Đặt cược", "Phiên " + this.referenceId + ": đặt " + betSideStr + " (" + inputTime + ")", 0L, this.referenceId, TransType.START_TRANS);
                        try {
                            BetTXMD5Message message = new BetTXMD5Message();
                            message.setBetSide(betSide);
                            message.setNickname(nickname);
                            message.setBetValue(betValue);
                            message.setReferenceId(referenceId);
                            message.setUserId(userId);
                            RMQApi.publishMessage("queue_message_tx", message, 55);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        res.setSuccess(true);
                    }
                    if (res.isSuccess()) {
                        if (!this.enableBetting) { // kiểm tra xem có phải đang trong quá trình đặt cược hay ko , nếu ko
                            result = 1;
                            if (!isBot) { // hoàn trả tiền cược
                                this.userService.updateMoney(nickname, betValue, this.moneyTypeStr, Games.TAI_XIU_KUBET.getName(), "Tài xỉu: Trả cược", "Hoàn trả đặt cược phiên " + this.referenceId, 0L, this.referenceId, TransType.END_TRANS);
                            }
                        } else { // nếu đang trong quá trình đặt cược
                            isBot = this.isBot(nickname);
                            if (moneyType == 1 && !isBot) {
                                if (betValue >= (long) ConfigGame.getIntValue("tx_min_money_black_list", 2000000) && ConfigGame.inBlackList(nickname) && new Random().nextInt(100) <= ConfigGame.getIntValue("tx_black_list_percent", 50)) {
                                    Debug.trace("Black list " + nickname + " money= " + betValue + ", bet side= " + betSide);
                                }
                                if (betValue >= (long) ConfigGame.getIntValue("tx_min_money_white_list", 2000000) && ConfigGame.inWhiteList(nickname) && new Random().nextInt(100) <= ConfigGame.getIntValue("tx_white_list_percent", 50)) {
                                    Debug.trace("White list " + nickname + " money= " + betValue + ", bet side= " + betSide);
                                }
                            }
                            switch (betSide) {
                                case 0:
                                    this.potXiu.bet(transTX, isBot);
                                    break;
                                case 1:
                                    this.potTai.bet(transTX, isBot);
                                    break;
                                case 2:
                                    this.potChan.bet(transTX, isBot);
                                    break;
                                case 3:
                                    this.potLe.bet(transTX, isBot);
                                    break;
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
            } else {
                result = 4;
            }
        }
        BetTaiXiuMsg msg = new BetTaiXiuMsg();
        msg.Error = (byte) result;
        msg.currentMoney = currentMoney; // số tiền hiện tại

        return msg;
    }

    public void updateTaiXiuPerSecond(int amountBotTaiFake, int amountBotXiuFake, int amountBotChanFake, int amountBotLeFake) {
        UpdateTaiXiuPerSecondMsg msg = new UpdateTaiXiuPerSecondMsg();
        msg.remainTime = this.getRemainTime();
        msg.bettingState = this.bettingRound;
        msg.potTai = this.getPotTai();
        msg.potXiu = this.getPotXiu();
        msg.potChan = this.getPotChan();
        msg.potLe = this.getPotLe();
        msg.numBetTai = (this.potTai.getNumBet() + amountBotTaiFake);
        msg.numBetXiu = (this.potXiu.getNumBet() + amountBotXiuFake);
        msg.numBetChan = (this.potChan.getNumBet() + amountBotChanFake);
        msg.numBetLe = (this.potLe.getNumBet() + amountBotLeFake);
        msg.moneyHu = TaiXiuModule.moneyHu;
        cacheService.setValue("kubet_Lobby_tx_tai_" + this.moneyType, String.valueOf(this.getPotTai()));
        cacheService.setValue("kubet_Lobby_tx_xiu_" + this.moneyType, String.valueOf(this.getPotXiu()));
        cacheService.setValue("kubet_Lobby_tx_chan_" + this.moneyType, String.valueOf(this.getPotChan()));
        cacheService.setValue("kubet_Lobby_tx_le_" + this.moneyType, String.valueOf(this.getPotLe()));
        this.sendMessageToRoom(msg);
    }

    // todo : tính toán kết quả
    public void calculatePrize(long referenceId) {
        PotTaiXiu potX = this.potXiu;
        PotTaiXiu potT = this.potTai;
        PotTaiXiu potChan = this.potChan;
        PotTaiXiu potLe = this.potLe;

        HashMap<String, TransactionTaiXiu> sumTXTMap = new HashMap<>();
        HashMap<String, TransactionTaiXiu> sumTai = new HashMap<>();
        HashMap<String, TransactionTaiXiu> sumXiu = new HashMap<>();
        HashMap<String, TransactionTaiXiu> sumChan = new HashMap<>();
        HashMap<String, TransactionTaiXiu> sumLe = new HashMap<>();

        long totalDice = this.resultTX.dice1 + this.resultTX.dice2 + this.resultTX.dice3;
        ResultTaiXiu rs = this.resultTX;
        Debug.trace("resultTX {}", this.resultTX);

        // Tính toán tiền thắng thua trong game cho 2 mặt TÀI/XỈU
        switch (this.result) {
            case 0: { // kết quả xỉu
                if (potX != null && potX.contributors != null) { // tính toán tiền thắng cược bên xỉu
                    for (TransactionTaiXiuDetail tran : potX.contributors) {
                        try {

                            // giải thưởng, hay nói cách khác là số tiền thắng
                            tran.prize = Math.round((long) ((float) tran.betValue * (100.0f - this.tax) / 100.0f) + tran.betValue);

                            // Cộng dồn để tính tổng số tiền trả lại
                            rs.totalPrize += tran.prize;



                            // Tổng tiền lỗ lãi
                            this.updateSumTran(sumTXTMap, tran);
                            this.updateSumTran(sumXiu, tran);

                            // Update giao dịch
                            this.saveTransactionDetailTX(tran);
                        } catch (Exception e) {
                            Debug.trace("Error calculate prize user " + tran.username + " error: " + e.getMessage());
                        }
                    }
                }
                // trừ tiền mấy thằng đánh tài
                if (potT == null || potT.contributors == null) {
                    break;
                }
                for (TransactionTaiXiuDetail tran : potT.contributors) {
                    try {


                        // Tổng tiền lỗ lãi
                        this.updateSumTran(sumTXTMap, tran);
                        this.updateSumTran(sumTai, tran);

                        // Update giao dịch
                        this.saveTransactionDetailTX(tran);
                    } catch (Exception e) {
                        Debug.trace("Error calculate prize user " + tran.username + " error: " + e.getMessage());
                    }
                }
                break;
            }
            case 1: { // kết quả về tài
                if (potT != null && potT.contributors != null) {
                    for (TransactionTaiXiuDetail tran : potT.contributors) {
                        try {


                            tran.prize = Math.round((long) ((float) tran.betValue * (100.0f - this.tax) / 100.0f) + tran.betValue);

                            rs.totalPrize += tran.prize;


                            // Tổng tiền lỗ lãi
                            this.updateSumTran(sumTXTMap, tran);
                            this.updateSumTran(sumTai, tran);

                            // Update giao dịch
                            this.saveTransactionDetailTX(tran);
                        } catch (Exception e) {
                            Debug.trace("Error calculate prize user " + tran.username + " error: " + e.getMessage());
                        }
                    }
                }
                if (potX == null || potX.contributors == null) break;
                for (TransactionTaiXiuDetail tran : potX.contributors) {
                    try {


                        // Tổng tiền lỗ lãi
                        this.updateSumTran(sumTXTMap, tran);
                        this.updateSumTran(sumXiu, tran);

                        // Update giao dịch
                        this.saveTransactionDetailTX(tran);
                    } catch (Exception e) {
                        Debug.trace("Error calculate prize user " + tran.username + " error: " + e.getMessage());
                    }
                }
                break;
            }
            default: {
                Debug.trace("Fuck error TX, room=" + this.moneyTypeStr + ", reference= " + referenceId + ", result= " + this.result);
            }
        }

        // Tính toán tiền thắng thua trong game cho 2 mặt CHẴN/LẺ
        boolean isChan = resultTX.isChan();
        int chanOrLe = isChan ? 1 : 0;
        switch (chanOrLe) {
            case 0: { // kết quả xỉu
                if (potLe != null && potLe.contributors != null) { // tính toán tiền thắng cược bên lẻ
                    for (TransactionTaiXiuDetail tran : potLe.contributors) {
                        try {


                            // giải thưởng, hay nói cách khác là số tiền thắng
                            tran.prize = Math.round((long) ((float) tran.betValue * (100.0f - this.tax) / 100.0f) + tran.betValue);

                            // Cộng dồn để tính tổng số tiền trả lại
                            rs.totalPrize += tran.prize;

                            // Tổng tiền lỗ lãi
                            this.updateSumTran(sumTXTMap, tran);
                            this.updateSumTran(sumLe, tran);

                            // Update giao dịch
                            this.saveTransactionDetailTX(tran);
                        } catch (Exception e) {
                            Debug.trace("Error calculate prize user " + tran.username + " error: " + e.getMessage());
                        }
                    }
                }
                // trừ tiền mấy thằng đánh tài
                if (potChan == null || potChan.contributors == null) {
                    break;
                }
                for (TransactionTaiXiuDetail tran : potChan.contributors) {
                    try {
                        // Tổng tiền lỗ lãi
                        this.updateSumTran(sumTXTMap, tran);
                        this.updateSumTran(sumChan, tran);

                        // Update giao dịch
                        this.saveTransactionDetailTX(tran);
                    } catch (Exception e) {
                        Debug.trace("Error calculate prize user " + tran.username + " error: " + e.getMessage());
                    }
                }
                break;
            }
            case 1: { // kết quả về tài
                if (potChan != null && potChan.contributors != null) {
                    for (TransactionTaiXiuDetail tran : potChan.contributors) {
                        try {


                            tran.prize = Math.round((long) ((float) tran.betValue * (100.0f - this.tax) / 100.0f) + tran.betValue);

                            rs.totalPrize += tran.prize;


                            // Tổng tiền lỗ lãi
                            this.updateSumTran(sumTXTMap, tran);
                            this.updateSumTran(sumChan, tran);

                            // Update giao dịch
                            this.saveTransactionDetailTX(tran);
                        } catch (Exception e) {
                            Debug.trace("Error calculate prize user " + tran.username + " error: " + e.getMessage());
                        }
                    }
                }
                if (potLe == null || potLe.contributors == null) break;
                for (TransactionTaiXiuDetail tran : potLe.contributors) {
                    try {
                        // Tổng tiền lỗ lãi
                        this.updateSumTran(sumTXTMap, tran);
                        this.updateSumTran(sumLe, tran);

                        // Update giao dịch
                        this.saveTransactionDetailTX(tran);
                    } catch (Exception e) {
                        Debug.trace("Error calculate prize user " + tran.username + " error: " + e.getMessage());
                    }
                }
                break;
            }
            default: {
                Debug.trace("Fuck error TX, room=" + this.moneyTypeStr + ", reference= " + referenceId + ", result= " + this.result);
            }
        }

        ArrayList<TransactionTaiXiu> trans = new ArrayList<>(sumTXTMap.values());
        if (this.moneyType == 1) {
            logger.trace("TX phien= " + referenceId + ", tinh toan xong ket qua");

//            //Xử lý khi nổ hũ
            if (totalDice == 3 || totalDice == 18) {
                rs.statusHu = 0;
            }
        }

        assert potT != null;
        rs.totalTai = potT.getTotalValue();
        rs.numBetTai = potT.getNumBet();
        assert potX != null;
        rs.totalXiu = potX.getTotalValue();
        rs.numBetXiu = potX.getNumBet();
        assert potChan != null;
        rs.totalChan = potChan.getTotalValue();
        rs.numBetChan = potChan.getNumBet();
        assert potLe != null;
        rs.totalLe = potLe.getTotalValue();
        rs.numBetLe = potLe.getNumBet();
        rs.moneyHu = TaiXiuModule.moneyHu;
        rs.moneyType = this.moneyType;
        rs.kubetSessionId = module.kubetSessionId;

        // Tính tiền và trả lại cho khách
        new UpdateMoneyTXTask(sumTai).start();
        if (this.moneyType == 1) {
            Debug.trace("TX phien= " + referenceId + ", cap nhat xong ben tai");
        }

        new UpdateMoneyTXTask(sumXiu).start();
        if (this.moneyType == 1) {
            Debug.trace("TX phien= " + referenceId + ", cap nhat xong ben xiu");
        }

        new UpdateMoneyTXTask(sumChan).start();
        if (this.moneyType == 1) {
            Debug.trace("TX phien= " + referenceId + ", cap nhat xong ben chan");
        }

        new UpdateMoneyTXTask(sumLe).start();
        if (this.moneyType == 1) {
            Debug.trace("TX phien= " + referenceId + ", cap nhat xong ben le");
        }

        try {
            // lưu kết quả tài xỉu
            Debug.trace("Ket qua của phiên sẽ lưu ");
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
            tran.kubetSessionId = tranDetail.kubetSessionId;
            map.put(tranDetail.username, tran);
        }
    }

    /**
     * Lưu transaction một người dùng mỗi phiên
     *
     * @param tran TransactionTaiXiuDetail
     */
    private void saveTransactionDetailTX(TransactionTaiXiuDetail tran) {
        try {
            this.taiXiuService.saveTransactionTaiXiuDetail(tran);
        } catch (Exception e) {
            Debug.trace("Update transaction detail tai xiu error: " + e.getMessage());
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
        msg.potChan = this.getPotChan();
        msg.potLe = this.getPotLe();
        msg.myBetChan = this.getTotalBettingChanByUsername(user.getName());
        msg.myBetLe = this.getTotalBettingLeByUsername(user.getName());
        msg.moneyHu = TaiXiuModule.moneyHu;
        if (this.resultTX != null
                && !bettingRound
                && module.count >= TaiXiuModule.TOTAL_BETTING_TIME) {
            msg.dice1 = (short) this.resultTX.dice1;
            msg.dice2 = (short) this.resultTX.dice2;
            msg.dice3 = (short) this.resultTX.dice3;
        }
        msg.remainTimeRutLoc = remainTimeRutLoc;
        this.sendMessageToUser(msg, user);
    }

    public boolean isBetting() {
        return this.bettingRound;
    }

    public long getPotTai() {
        return this.potTai.getTotalValue();
    }

    public long getPotChan() {
        return this.potChan.getTotalValue();
    }

    public long getPotLe() {
        return this.potLe.getTotalValue();
    }


    public long getUserBetTai() {
        return this.potTai.getTotalValue() - this.potTai.getTotalBotBet();
    }

    public long getUserBetChan() {
        return this.potChan.getTotalValue() - this.potChan.getTotalBotBet();
    }

    public long getUserBetLe() {
        return this.potLe.getTotalValue() - this.potLe.getTotalBotBet();
    }

    public List<TaiXiuAdmin> getListRealTransaction() {
        return Stream.of(this.potTai.getContributorNotBot(), this.potXiu.getContributorNotBot(), potChan.getContributorNotBot(), potLe.getContributorNotBot()).flatMap(Collection::stream).collect(Collectors.toList());
    }


    public long getTotalMoneyTai() {
        return this.potTai.getTotalValue();
    }

    public long getTotalMoneyXiu() {
        return this.potXiu.getTotalValue();
    }

    public long getTotalMoneyChan() {
        return this.potChan.getTotalValue();
    }

    public long getTotalMoneyLe() {
        return this.potLe.getTotalValue();
    }


    public long getNumberUserRealTai() {
        return this.potTai.getNumberUserNotBot();
    }

    public long getNumberUserRealXiu() {
        return this.potXiu.getNumberUserNotBot();
    }

    public long getNumberUserRealChan() {
        return this.potChan.getNumberUserNotBot();
    }

    public long getNumberUserRealLe() {
        return this.potLe.getNumberUserNotBot();
    }

    public long getNumberUserAndBotTai() {
        return this.potTai.getNumberUserBot();
    }

    public long getNumberUserAndBotXiu() {
        return this.potXiu.getNumberUserBot();
    }

    public long getNumberUserAndBotChan() {
        return this.potChan.getNumberUserBot();
    }

    public long getNumberUserAndBotLe() {
        return this.potLe.getNumberUserBot();
    }

    public long getPotXiu() {
        return this.potXiu.getTotalValue();
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

    public long getTotalBettingChanByUsername(String username) {
        return this.potChan.getTotalBetByUsername(username);
    }

    public long getTotalBettingLeByUsername(String username) {
        return this.potLe.getTotalBetByUsername(username);
    }

    public boolean isBot(String username) {
        UserCacheModel model = this.userService.getUser(username);
        return model.isBot();
    }

    public static short getMoneyType(int roomId) {
        return roomId == 0 ? (short) 0 : 1;
    }

    public static String getKeyRoom(short moneyType) {
        return moneyType + "_" + 2;
    }

    @Override
    public boolean joinRoom(User user) {
        boolean result = super.joinRoom(user);
        if (result) {
            user.setProperty("MGROOM_TAI_XIU_INFO", this);
        }
        return result;
    }

    @Override
    public boolean quitRoom(User user) {
        boolean result = super.quitRoom(user);
        if (result) {
            user.removeProperty("MGROOM_TAI_XIU_INFO");
        }
        return result;
    }

    @Override
    protected void checkResetPot() {
        //todo : check reset pot
    }

    private final class UpdateMoneyTXTask extends Thread {
        private final Map<String, TransactionTaiXiu> trans;

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
                        MGRoomTaiXiu.this.userService.updateMoney(username, 0L, MGRoomTaiXiu.this.moneyTypeStr, Games.TAI_XIU_KUBET.getName(), "", "", 0L, MGRoomTaiXiu.this.referenceId, TransType.END_TRANS);
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
                                res2 = MGRoomTaiXiu.this.userService.updateMoney(username, txt.totalPrize, MGRoomTaiXiu.this.moneyTypeStr, Games.TAI_XIU_KUBET.getName(), "Thắng tài xỉu", "Phiên " + MGRoomTaiXiu.this.referenceId, fee, MGRoomTaiXiu.this.referenceId, transType);
                            } else {
                                res2.setSuccess(true);
                            }
                            if (res2.isSuccess()) {
                                currentMoney = res2.getCurrentMoney();
                                long totalExchange = Math.round((long) ((float) txt.totalPrize * (100.0f - MGRoomTaiXiu.this.tax) / (200.0f - MGRoomTaiXiu.this.tax)));
                                if (MGRoomTaiXiu.this.moneyType == 1 && totalExchange >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                                    MGRoomTaiXiu.this.broadcastMsgService.putMessage(Games.TAI_XIU_KUBET.getId(), username, totalExchange);
                                }
                            }
                        }
                        if (txt.totalRefund > 0L) {
                            if (!MGRoomTaiXiu.this.isBot(username)) {
                                res = MGRoomTaiXiu.this.userService.updateMoney(username, txt.totalRefund, MGRoomTaiXiu.this.moneyTypeStr, Games.TAI_XIU_KUBET.getName(), "Hoàn trả tài xỉu", "Phiên " + MGRoomTaiXiu.this.referenceId, 0L, MGRoomTaiXiu.this.referenceId, TransType.END_TRANS);
                                if (res.isSuccess()) {
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
                    MGRoomTaiXiu.this.sendMessageToUser(msg, username);
                } catch (Exception e) {
                    Debug.trace("Update tai xiu money phien " + MGRoomTaiXiu.this.referenceId + " error: " + e.getMessage());
                }
            }
        }
    }
}

