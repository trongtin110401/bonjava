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
import com.vinplay.dal.dao.LogMoneyUserDao;
import com.vinplay.dal.dao.impl.LogMoneyUserDaoImpl;
import com.vinplay.dal.entities.report.ReportMoneySystemModel;
import com.vinplay.dal.entities.taixiu.ResultTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiu;
import com.vinplay.dal.entities.taixiu.TransactionTaiXiuDetail;
import com.vinplay.dal.service.BroadcastMessageService;
import com.vinplay.dal.service.CacheService;
import com.vinplay.dal.service.TaiXiuService;
import com.vinplay.dal.service.impl.AgentServiceImpl;
import com.vinplay.dal.service.impl.BroadcastMessageServiceImpl;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.dal.service.impl.TaiXiuServiceImpl;
import com.vinplay.usercore.service.UserService;
import com.vinplay.usercore.service.impl.UserServiceImpl;
import com.vinplay.utils.AdminSocketAlert;
import com.vinplay.utils.TelegramAlert;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.hazelcast.HazelcastClientFactory;
import com.vinplay.vbee.common.messages.BetTXMD5Message;
import com.vinplay.vbee.common.models.UserModel;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.response.AgentResponse;
import com.vinplay.vbee.common.response.LogUserMoneyResponse;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.response.minigame.TaiXiuAdmin;
import com.vinplay.vbee.common.rmq.RMQApi;
import com.vinplay.vbee.common.statics.TransType;
import game.modules.minigame.TaiXiuModule;
import game.modules.minigame.cmd.rev.BetTaiXiuCmd;
import game.modules.minigame.cmd.send.*;
import game.modules.minigame.entities.BalanceMoneyTX;
import game.modules.minigame.entities.MinigameConstant;
import game.modules.minigame.entities.PotTaiXiu;
import game.modules.minigame.utils.RutLocUtils;
import game.modules.minigame.utils.TaiXiuUtils;
import game.utils.ConfigGame;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// todo : con của MGRoom
public class MGRoomTaiXiu
        extends MGRoom {
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
    public ResultTaiXiu resultTX;
    private TaiXiuService api = new TaiXiuServiceImpl(); // tài xỉu service lấy cả trong rabbitmq và cả trong cache server
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

    private TaiXiuModule module;

    public MGRoomTaiXiu(String name, long referenceId, short moneyType, TaiXiuModule module) {
        super(name);
        this.module = module;
        this.moneyType = moneyType;
        this.moneyTypeStr = "xu";
        if (moneyType == 1) {
            this.moneyTypeStr = "vin";
            this.tax = MinigameConstant.MINIGAME_TAX_VIN;
            ReportMoneySystemModel model = this.api.getReportTX(ConfigGame.getIntValue("interval_reset_balance", 10));
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
        this.resultTX = null;
        this.startTime = System.currentTimeMillis();
        this.bettingRound = false;
        try {
            this.cacheService.removeKey("allow_betting_" + this.referenceId); // xóa key cho phép đặt tài xỉu
            this.cacheService.removeKey("force_result_" + this.referenceId); // xóa key kết quả bắt buộc
        } catch (Exception e) {

        }

    }

    // todo : không cho phép đặt cược
    public void disableBetting() {
        this.enableBetting = false;
        this.cacheService.setValue("allow_betting_" + this.referenceId, 0); // không cho phép đặt cược nữa
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

    // todo : lấy thời gian còn lại bằng cách lấy thời gian hiện tại trừ đi thời gian bắt đầu
    public short getRemainTime() {
        long currentTime = System.currentTimeMillis();
        int remainTime = (int) ((currentTime - this.startTime) / 1000L);
        if (remainTime < 0) {
            remainTime = 0;
        } else if (remainTime > 50) {
            remainTime = 50;
        }

        if (this.bettingRound) {
            return (short) (51 - this.module.count);
        }
        return (short) (65 - this.module.count);
    }

    // todo : bet tài xỉu
    public void betTaiXiu(User user, BetTaiXiuCmd cmd) {
        BetTaiXiuMsg msg = this.betTaiXiu(user.getName(), cmd.userId, cmd.betValue, cmd.inputTime, cmd.moneyType, cmd.betSide, false);
        this.sendMessageToUser((BaseMsg) msg, user); // todo : gửi message về client
        try {
            if (cmd.betValue <= 0) {
                return;
            }
            BetTXMD5Message message = new BetTXMD5Message();
            message.setBetSide(cmd.betSide);
            message.setNickname(user.getName());
            message.setBetValue(cmd.betValue);
            message.setReferenceId(cmd.referenceId);
            message.setUserId(user.getId());
            RMQApi.publishMessage("queue_message_tx", message, 50);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean CheckQuota(String nick_name, boolean seven_days) {
        try {
            UserModel userModel = userService.getUserByNickName(nick_name);
            if (userModel != null) {
                if (userModel.getDaily() == 1 || userModel.getDaily() == 2) {
                    return true;
                }
                // nap the
                LogMoneyUserDao logService = new LogMoneyUserDaoImpl();
                long total_giftcode_money = 0;
                long total_user_receive = 0;
                long total_agency_receive = 0;
                long total_user_transfer = 0;
                long total_agency_transfer = 0;

                AgentServiceImpl service = new AgentServiceImpl();
                List<AgentResponse> agents = service.listAgent();
                ArrayList<String> agentNames = new ArrayList<String>();
                if (agents != null && agents.size() > 0) {
                    for (AgentResponse agent : agents) {
                        agentNames.add(agent.nickName);
                    }
                }
                List<LogUserMoneyResponse> resultGiftCode = logService.searchAllLogMoneyUser(nick_name, "GIFTCODE", seven_days);
                if (resultGiftCode != null && resultGiftCode.size() > 0) {
                    total_giftcode_money = resultGiftCode.stream().map((trans) -> trans.moneyExchange).reduce(total_giftcode_money, (accumulator, _item) -> accumulator + _item);
                }
                List<LogUserMoneyResponse> resulReceive = logService.searchAllLogMoneyUser(nick_name, "RECEIVE", seven_days);
                if (resulReceive != null && resulReceive.size() > 0) {
                    for (LogUserMoneyResponse trans : resulReceive) {
                        boolean matchAgent = false;
                        for (String s : agentNames) {
                            if (trans.description.contains(s)) {
                                matchAgent = true;
                            }
                        }
                        if (matchAgent) {
                            total_agency_receive += trans.moneyExchange;
                        } else {
                            total_user_receive += trans.moneyExchange;
                        }
                    }
                }
                long total_recharge_card_money = 0;//total_agency_receive - userModel.getRechargeMoney();
                List<LogUserMoneyResponse> resultCard = logService.searchAllLogMoneyUser(nick_name, "CARD", seven_days);
                if (resultCard != null && resultCard.size() > 0) {
                    total_recharge_card_money = resultCard.stream().map((trans) -> trans.moneyExchange).reduce(total_recharge_card_money, (accumulator, _item) -> accumulator + _item);
                }

                boolean isValid = false;
                if ((total_recharge_card_money + total_agency_receive) >= 100000 || userModel.getVinTotal() > 100000) {
                    isValid = true;
                }
                return isValid;
            }
        } catch (Exception ex) {
            Debug.trace((Object) ("Check quote error : " + ex.getMessage()));
        }
        return false;
    }

    // todo : đặt tài xỉu
    public BetTaiXiuMsg betTaiXiu(String nickname, int userId, long betValue, short inputTime, short moneyType, short betSide, boolean isBot) {
        if (!isBot && moneyType == 1) {
            if (betSide == 1) {
                soTienNguoiChoiDatTai += betValue; // người chơi đặt tài
            } else {
                soTienNguoiChoiDatXiu += betValue; // người đặt xỉu
            }
        }
        inputTime = this.getRemainTime(); // lấy thời gian còn lại
        long currentMoney = this.userService.getMoneyUserCache(nickname, this.moneyTypeStr);  // lấy số tiền hiện tại của user dựa trên nick name
        int result = 2;
        if (this.enableBetting) {
            if (betValue >= 100L) {
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
                            res = this.userService.updateMoney(nickname, -betValue, this.moneyTypeStr, "TaiXiu", "T\u00e0i x\u1ec9u: \u0110\u1eb7t c\u01b0\u1ee3c", "Phii\u00ean " + this.referenceId + ": \u0111\u1eb7t " + betSideStr + " (" + inputTime + ")", 0L, Long.valueOf(this.referenceId), TransType.START_TRANS);

                        } else {
                            res.setSuccess(true);
                        }
                        if (res.isSuccess()) {
                            if (!this.enableBetting) { // kiểm tra xem có phải đang trong quá trình đặt cược hay ko , nếu ko
                                result = 1;
                                if (!isBot) { // hoàn trả tiền cược
                                    this.userService.updateMoney(nickname, betValue, this.moneyTypeStr, "TaiXiu", "T\u00e0i x\u1ec9u: Tr\u1ea3 c\u01b0\u1ee3c", "Ho\u00e0n tr\u1ea3 \u0111\u1eb7t c\u01b0\u1ee3c phi\u00ean " + this.referenceId, 0L, Long.valueOf(this.referenceId), TransType.END_TRANS);

                                }
                            } else { // nếu đang trong quá trình đặt cược
                                isBot = this.isBot(nickname);
                                if (moneyType == 1 && !isBot) {
                                    Random rd;
                                    int n;
                                    this.balance.addBet(betValue); //
                                    if (betValue >= (long) ConfigGame.getIntValue("tx_min_money_black_list", 2000000) && ConfigGame.inBlackList(nickname) && (n = (rd = new Random()).nextInt(100)) <= ConfigGame.getIntValue("tx_black_list_percent", 50)) {
                                        Debug.trace((Object) ("Black list " + nickname + " money= " + betValue + ", bet side= " + betSide));
                                        if (betSide == 1) {
                                            this.blackListBetTai += betValue;
                                        } else {
                                            this.blackListBetXiu += betValue;
                                        }
                                    }
                                    if (betValue >= (long) ConfigGame.getIntValue("tx_min_money_white_list", 2000000) && ConfigGame.inWhiteList(nickname) && (n = (rd = new Random()).nextInt(100)) <= ConfigGame.getIntValue("tx_white_list_percent", 50)) {
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

    public void updateTaiXiuPerSecond(int amountBotTaiFake, int amountBotXiuFake) {
        UpdateTaiXiuPerSecondMsg msg = new UpdateTaiXiuPerSecondMsg();
        msg.remainTime = this.getRemainTime();
        msg.bettingState = this.bettingRound;
        msg.potTai = this.getPotTai();
        msg.potXiu = this.getPotXiu();

        msg.numBetTai = (this.potTai.getNumBet() + amountBotTaiFake);
        msg.numBetXiu = (this.potXiu.getNumBet() + amountBotXiuFake);
        msg.moneyHu = TaiXiuModule.moneyHu;
        cacheService.setValue("Lobby_tx_tai_" + this.moneyType, String.valueOf(this.getPotTai()));
        cacheService.setValue("Lobby_tx_xiu_" + this.moneyType, String.valueOf(this.getPotXiu()));
        this.sendMessageToRoom(msg);
    }

    //todo : gợi ý kết quả , kiểm tra admin có set kết quả ko
    public short suggestResult() {
        if (this.moneyType != 1) return -1;

        //CHECK admin set result  //

        CacheServiceImpl sv = new CacheServiceImpl();
        int result = sv.getValueIntWithDefault("force_result_" + this.referenceId); // set kết quả tài xỉu trong cache
        if (result > -1) {
            return (short) result;
        }
        int configForceResult = 1;
        try {
            configForceResult = ConfigGame.getIntValue(ConfigGame.TaiXiuForceResult); // lấy từ config game
            if (configForceResult == -1) {
                return -1;
            }
        } catch (Exception e) {

        }


        PotTaiXiu potX = this.potXiu;
        PotTaiXiu potT = this.potTai;
        long tongTienHopLe = (potT == null || potX == null) ? 0 : (potT.getTotalValue() > potX.getTotalValue() ? potX.getTotalValue() : potT.getTotalValue());
        long tongTienTaiDaTinh = 0L;  // tổng tiền tài đã tính
        long tongTienXiuDaTinh = 0L;  // tổng tiền xỉu

        long totalCashIn = 0L;
        long totalCashOut = 0L;

        // xiu win
        if (potX != null && potX.contributors != null) {
            for (TransactionTaiXiuDetail tran : potX.contributors) {
                if (tran.userId == 0) continue;
                long tienDuocTinh = tran.betValue;
                totalCashIn += tienDuocTinh;
                if (tongTienXiuDaTinh + tran.betValue > tongTienHopLe) {
                    tienDuocTinh = tongTienHopLe - tongTienXiuDaTinh;
                }
                tongTienXiuDaTinh += tienDuocTinh;
                totalCashOut += Math.round((long) ((float) tienDuocTinh * (100.0f - this.tax) / 100.0f) + tienDuocTinh);
                totalCashOut += tran.betValue - tienDuocTinh;
            }
        }
        if (!(potT == null || potT.contributors == null))
            for (TransactionTaiXiuDetail tran : potT.contributors) {
                if (tran.userId == 0) continue;
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
                if (tran.userId == 0) continue;
                long tienDuocTinh = tran.betValue;
                totalCashIn += tienDuocTinh;
                if (tongTienTaiDaTinh + tran.betValue > tongTienHopLe) {
                    tienDuocTinh = tongTienHopLe - tongTienTaiDaTinh;
                }
                tongTienTaiDaTinh += tienDuocTinh;
                totalCashOut += Math.round((long) ((float) tienDuocTinh * (100.0f - this.tax) / 100.0f) + tienDuocTinh);
                totalCashOut += tran.betValue - tienDuocTinh;
            }
        }
        if (!(potX == null || potX.contributors == null))
            for (TransactionTaiXiuDetail tran : potX.contributors) {
                if (tran.userId == 0) continue;
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
            IMap bankMap = client.getMap("txBank");
            String key = "txBank:" + this.moneyType;
            long bank = 0L;
            //bankMap.lock(key);
            if (bankMap.containsKey(key)) {
                bank = (long) bankMap.get(key);
            } else {
                bankMap.put(key, 0L);
            }
            logger.error("suggestResult key:" + key + "bank:" + bank);

            bankMap.put("deltaXiu", deltaXiu);
            bankMap.put("deltaTai", deltaTai);

            //bankMap.unlock(key);
            Debug.trace((Object) ("tx bank: " + bank));
            // enough bank
            if ((deltaXiu > 0 && deltaTai > 0) || (deltaXiu < 0 && bank + deltaXiu >= 0) || (deltaTai < 0 && bank + deltaTai >= 0)) {
                return (short) (ThreadLocalRandom.current().nextInt(0, 1000560000) % 2);
            }
            if (deltaXiu > 0 && deltaTai < 0) // let xiu win
            {
                return 0;
            }
            if (deltaXiu < 0 && deltaTai > 0) // let tai win
            {
                return 1;
            }
        } catch (Exception ex) {
            logger.error("suggestResult:" + ex.getMessage());
            Debug.trace((Object) (" error resultTX: " + ex.getMessage()));
        }

        return (short) (ThreadLocalRandom.current().nextInt(0, 1000560000) % 2); // random
    }

    // todo : tính toán kết quả
    public void calculatePrize(long referenceId) {
        PotTaiXiu potX = this.potXiu;
        PotTaiXiu potT = this.potTai;
        HashMap<String, TransactionTaiXiu> sumTXTMap = new HashMap<String, TransactionTaiXiu>();
        HashMap<String, TransactionTaiXiu> sumTai = new HashMap<String, TransactionTaiXiu>();
        HashMap<String, TransactionTaiXiu> sumXiu = new HashMap<String, TransactionTaiXiu>();
        List<MoneyHuUser> lstUserHu = new ArrayList<>();
        long tongTienHopLe = (potT == null || potX == null) ? 0 : (Math.min(potT.getTotalValue(), potX.getTotalValue()));
        long tongTienTaiDaTinh = 0L;
        long tongTienXiuDaTinh = 0L;

        long totalCashIn = 0L;
        long totalCashOut = 0L;
        long totalDice = this.resultTX.dice1 + this.resultTX.dice2 + this.resultTX.dice3;
        StringBuilder userNameHu = new StringBuilder();
        StringBuilder moneyUserHu = new StringBuilder();
        ResultTaiXiu rs = new ResultTaiXiu();
        try {
            if (this.resultTX != null) {
                rs = this.resultTX;
                Debug.trace((Object) ("resultTX " + (Object) this.resultTX));
            } else {
                Debug.trace((Object) (" error: " + (Object) this.resultTX));
            }
        } catch (Exception ex) {
            Debug.trace((Object) (" error resultTX: " + ex.getMessage()));
        }
        switch (this.result) {
            case 0: {
                if (potX != null && potX.contributors != null) {

                    for (TransactionTaiXiuDetail tran : potX.contributors) {
                        try {
                            //số tiền mà người chơi sẽ phải bỏ vào tính thắng thua
                            long tienDuocTinh = tran.betValue;
                            // Nếu không phải robot thì tính tổng tiền đầu vào của khách thật
                            if (tran.userId != 0)
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
                            tran.prize = Math.round((long) ((float) tienDuocTinh * (100.0f - this.tax) / 100.0f) + tienDuocTinh);
                            //kiểm tra nếu có nổ hũ thì tính cộng thêm tiền nổ hũ
//                            if (totalDice == 3 || totalDice == 18) {
//                                tran.prize += (tienDuocTinh * TaiXiuModule.moneyHu / tongTienHopLe);
//                            }
                            // Cộng dồn để tính tổng số tiền trả lại
                            rs.totalPrize += tran.prize;
                            // Nếu không phải robot thì tính tổng tiền đầu ra phải trả cho khách thật
                            if (tran.userId != 0)
                                totalCashOut += tran.prize;
                            //Số tiền đặ trả lại không tính vào phiên
                            tran.refund = tran.betValue - tienDuocTinh;
                            rs.totalRefundXiu += tran.refund;
                            if (tran.userId != 0)
                                totalCashOut += tran.refund;
//                            if (totalDice != 3 & totalDice != 18) {
//                                TaiXiuModule.moneyHu += (long) (PHAN_TRAM_TIEN_HU * (tran.prize - tienDuocTinh));
//                            }
                            //Tổng tiền lỗ lãi
                            //tran.totalExchange = tran.prize - tienDuocTinh;
                            this.updateSumTran(sumTXTMap, tran);
                            this.updateSumTran(sumXiu, tran);
                            this.saveTransactionDetailTX(tran);
                            if (!isBot(tran.username)) TaiXiuModule.fundTx -= tran.refund;
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
                        long tienDuocTinh = tran.betValue;
                        if (tran.userId != 0)
                            totalCashIn += tienDuocTinh;
                        if (tongTienTaiDaTinh + tran.betValue > tongTienHopLe) {
                            tienDuocTinh = tongTienHopLe - tongTienTaiDaTinh;
                        }
                        tongTienTaiDaTinh += tienDuocTinh;
                        tran.refund = tran.betValue - tienDuocTinh;
                        rs.totalRefundTai += tran.refund;
                        if (tran.userId != 0)
                            totalCashOut += tran.refund;
                        //tran.totalExchange = tran.prize -tienDuocTinh;
                        this.updateSumTran(sumTXTMap, tran);
                        this.updateSumTran(sumTai, tran);
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
                            long tienDuocTinh = tran.betValue;
                            if (tran.userId != 0)
                                totalCashIn += tienDuocTinh;
                            if (tongTienTaiDaTinh + tran.betValue > tongTienHopLe) {
                                tienDuocTinh = tongTienHopLe - tongTienTaiDaTinh;
                            }
                            tongTienTaiDaTinh += tienDuocTinh;
                            tran.prize = Math.round((long) ((float) tienDuocTinh * (100.0f - this.tax) / 100.0f) + tienDuocTinh);  //kiểm tra nếu có nổ hũ thì tính cộng thêm tiền nổ hũ
//                            if (totalDice == 3 || totalDice == 18) {
//                                tran.prize += (double) (tienDuocTinh * TaiXiuModule.moneyHu / tongTienHopLe);
//                            }
                            rs.totalPrize += tran.prize;
                            if (tran.userId != 0)
                                totalCashOut += tran.prize;
                            tran.refund = tran.betValue - tienDuocTinh;

                            /*//kiểm tra nếu có nổ hũ thì tính cộng thêm tiền nổ hũ
                            if (totalDice == 3 || totalDice == 18) {
                                totalCashOut += (tienDuocTinh * HU_TX / tongTienHopLe);
                            }*/
                            rs.totalRefundTai += tran.refund;

                            if (tran.userId != 0)
                                totalCashOut += tran.refund;
//                            if (totalDice != 3 & totalDice != 18) {
//                                TaiXiuModule.moneyHu += (long) (PHAN_TRAM_TIEN_HU * (tran.prize - tienDuocTinh));
//                            }
                            //Tổng tiền lỗ lãi
                            //tran.totalExchange = tran.prize - tienDuocTinh;
                            this.updateSumTran(sumTXTMap, tran);
                            this.updateSumTran(sumTai, tran);
                            this.saveTransactionDetailTX(tran);
                            if (!isBot(tran.username)) TaiXiuModule.fundTx -= tran.refund;
                        } catch (Exception e) {
                            Debug.trace((Object) ("Error calculate prize user " + tran.username + " error: " + e.getMessage()));
                        }
                    }
                }
                if (potX == null || potX.contributors == null) break;
                for (TransactionTaiXiuDetail tran : potX.contributors) {
                    try {
                        long tienDuocTinh = tran.betValue;
                        if (tran.userId != 0)
                            totalCashIn += tienDuocTinh;
                        if (tongTienXiuDaTinh + tran.betValue > tongTienHopLe) {
                            tienDuocTinh = tongTienHopLe - tongTienXiuDaTinh;
                        }
                        tongTienXiuDaTinh += tienDuocTinh;
                        tran.refund = tran.betValue - tienDuocTinh;
                        rs.totalRefundXiu += tran.refund;
                        if (tran.userId != 0)
                            totalCashOut += tran.refund;
                        //tran.totalExchange = tran.prize - tienDuocTinh;
                        this.updateSumTran(sumTXTMap, tran);
                        this.updateSumTran(sumXiu, tran);
                        //Update giao dịch
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
            //Xử lý khi nổ hũ
            if (totalDice == 3 || totalDice == 18) {
                try {
                    List<TransactionTaiXiu> lst;
                    Stream<TransactionTaiXiu> stream = trans.stream();
                    if (totalDice == 3) {
                        lst = stream.filter(i -> i.betSide == 0)
                                .collect(Collectors.toList());
                    } else {
                        lst = stream.filter(i -> i.betSide == 1)
                                .collect(Collectors.toList());
                    }
                    lst.sort(new Comparator<TransactionTaiXiu>() {
                        public int compare(TransactionTaiXiu o1, TransactionTaiXiu o2) {
                            return Long.compare(o1.betValue - o1.totalRefund, o2.betValue - o2.totalRefund);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                rs.statusHu = 1;
            }

            CalculateEndTXTask task = new CalculateEndTXTask(trans);
            task.start();

            try {
                HazelcastInstance client = HazelcastClientFactory.getInstance();
                IMap bankMap = client.getMap("txBank");
                String key = "txBank:" + this.moneyType;
                long bank = 0L;
                //bankMap.lock(key);
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

                //bankMap.unlock(key);
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
        // Tính tiền và trả lại cho khách
        UpdateMoneyTXTask taskTai = new UpdateMoneyTXTask(sumTai);
        taskTai.start();
        if (this.moneyType == 1) {
            Debug.trace((Object) ("TX phien= " + referenceId + ", cap nhat xong ben tai"));
        }
        UpdateMoneyTXTask taskXiu = new UpdateMoneyTXTask(sumXiu);
        taskXiu.start();
        if (this.moneyType == 1) {
            Debug.trace((Object) ("TX phien= " + referenceId + ", cap nhat xong ben xiu"));
        }
        try {
            //lưu kết quả tài xỉu
            Debug.trace((Object) ("Ket qua của phiên sẽ lưu "));
            this.api.saveResultTaiXiu(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //Save tổng giao dịch trên phiên
            if (this.api.saveTransactionTaiXiu(trans)) {
                Debug.info("Save thanh cong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class MoneyHuUser {
        String username;
        long money;

        public MoneyHuUser(String username, long money) {
            this.username = username;
            this.money = money;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public long getMoney() {
            return money;
        }

        public void setMoney(long money) {
            this.money = money;
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
        if ((float) (revenueUser + (long) ((float) maxUserWin * (100.0F - tax) / 100.0F)) >= (float) -fee * ConfigGame.getFloatValue("tx_min_fee", 1.0F)) {
            result = tinhCuaThang(totalPrizeBotTai, totalPrizeBotXiu);
            Debug.trace("Chong he thong am, force= " + result + ", max user win= " + maxUserWin);
        }
        return result;
    }

    /**
     * Lưu transaction một người dùng mỗi phiên
     *
     * @param tran
     */
    private void saveTransactionDetailTX(TransactionTaiXiuDetail tran) {
        try {
            this.api.saveTransactionTaiXiuDetail(tran);
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
        //todo: lấy hũ trong cache Done
//        try {
//            msg.moneyHu = Long.parseLong(cacheService.getValueStr("Hu_TX_" + this.moneyType));
//        } catch (KeyNotFoundException ex) {
//            msg.moneyHu = TaiXiuModule.moneyHu;
//        }
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
                        MGRoomTaiXiu.this.userService.updateMoney(username, 0L, MGRoomTaiXiu.this.moneyTypeStr, "TaiXiu", "", "", 0L, MGRoomTaiXiu.this.referenceId, TransType.END_TRANS);
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
                                res2 = MGRoomTaiXiu.this.userService.updateMoney(username, txt.totalPrize, MGRoomTaiXiu.this.moneyTypeStr, "TaiXiu", "Th\u1eafng t\u00e0i x\u1ec9u", "Phi\u00ean " + MGRoomTaiXiu.this.referenceId, fee, MGRoomTaiXiu.this.referenceId, transType);
                            } else {
                                res2.setSuccess(true);
                            }
                            if (res2.isSuccess()) {
                                if (MGRoomTaiXiu.this.moneyType == 1 && !MGRoomTaiXiu.this.isBot(username)) {
                                    MGRoomTaiXiu.this.balance.addWin(txt.totalPrize);
                                    MGRoomTaiXiu.this.balance.addFee(fee);
                                }
                                currentMoney = res2.getCurrentMoney();
                                long totalExchange = (long) ((float) txt.totalPrize * (100.0f - MGRoomTaiXiu.this.tax) / (200.0f - MGRoomTaiXiu.this.tax));
                                if (MGRoomTaiXiu.this.moneyType == 1 && totalExchange >= (long) BroadcastMessageServiceImpl.MIN_MONEY) {
                                    MGRoomTaiXiu.this.broadcastMsgService.putMessage(Games.TAI_XIU.getId(), username, totalExchange);
                                }
                            }
                        }
                        if (txt.totalRefund > 0L) {
                            if (!MGRoomTaiXiu.this.isBot(username)) {
                                res = MGRoomTaiXiu.this.userService.updateMoney(username, txt.totalRefund, MGRoomTaiXiu.this.moneyTypeStr, "TaiXiu", "Ho\u00e0n tr\u1ea3 t\u00e0i x\u1ec9u", "Phi\u00ean " + MGRoomTaiXiu.this.referenceId, 0L, Long.valueOf(MGRoomTaiXiu.this.referenceId), TransType.END_TRANS);
                                if (res.isSuccess()) {
                                    if (MGRoomTaiXiu.this.moneyType == 1) {
                                        MGRoomTaiXiu.this.balance.addWin(txt.totalRefund);
                                    }
                                    currentMoney = res.getCurrentMoney();
                                }
                            }
                        }
                        /*if (txt.totalRefund > 0L && (res = MGRoomTaiXiu.this.userService.updateMoney(username, txt.totalRefund, MGRoomTaiXiu.this.moneyTypeStr, "TaiXiu", "Ho\u00e0n tr\u1ea3 t\u00e0i x\u1ec9u", "Phi\u00ean " + MGRoomTaiXiu.this.referenceId, 0L, Long.valueOf(MGRoomTaiXiu.this.referenceId), TransType.END_TRANS)).isSuccess()) {
                            if (MGRoomTaiXiu.this.moneyType == 1 && !MGRoomTaiXiu.this.isBot(username)) {
                                MGRoomTaiXiu.this.balance.addWin(txt.totalRefund);
                            }

                        }*/
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

    private final class CalculateEndTXTask
            extends Thread {
        private List<TransactionTaiXiu> trans;

        private CalculateEndTXTask(List<TransactionTaiXiu> trans) {
            this.trans = trans;
        }

        @Override
        public void run() {
            try {
                MGRoomTaiXiu.this.api.calculateThanhDu(MGRoomTaiXiu.this.referenceId, this.trans, (int) MGRoomTaiXiu.this.result);
                for (TransactionTaiXiu tran : this.trans) {
                    if (tran.betValue - tran.totalRefund < 20000L) continue;
                    int soLuotThem = RutLocUtils.getLuotRutLoc(tran.betValue - tran.totalRefund);
                    int soLuotRut = MGRoomTaiXiu.this.api.updateLuotRutLoc(tran.username, soLuotThem);
                    UpdateRutLocMsg msg = new UpdateRutLocMsg();
                    msg.soLuotRut = soLuotRut;
                    MGRoomTaiXiu.this.sendMessageToUser((BaseMsg) msg, tran.username);
                }
            } catch (Exception e) {
                Debug.trace((Object) ("Error save tai xiu: " + e.getMessage()));
            }
        }
    }
}

