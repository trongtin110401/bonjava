package game.modules.slot.room;

import bitzero.server.entities.User;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.enums.Games;
import com.vinplay.vbee.common.models.cache.UserCacheModel;
import com.vinplay.vbee.common.models.slot.SlotFreeSpin;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.statics.TransType;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.slot.SlotModule;
import game.modules.slot.cmd.Slot25CommandCollection;
import game.modules.slot.cmd.send.slot25extend.Slot25BigWinMsg;
import game.modules.slot.cmd.send.slot25extend.Slot25FreeDailyMsg;
import game.modules.slot.cmd.send.slot25extend.Slot25MinimizeResultMsg;
import game.modules.slot.cmd.send.slot25extend.Slot25ResultMsg;
import game.modules.slot.entities.slot.AwardsOnLine;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.line25extend.Slot25ExtendAward;
import game.modules.slot.entities.slot.line25extend.Slot25ExtendAwards;
import game.modules.slot.entities.slot.line25extend.Slot25ExtendItem;
import game.modules.slot.listener.SlotLogListener;
import game.modules.slot.utils.Slot25ExtendUtil;
import game.modules.slot.utils.SlotUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class SlotBLCRoom extends Slot25ExtendRoom {

    public SlotBLCRoom(SlotModule module, Slot25CommandCollection commandCollection, SlotLogListener logListener, String gameName, byte id, String room, short moneyType, long pot, long fund, int betValue, long initJackpotValue) {
        super(module, commandCollection, logListener, gameName, id, room, moneyType, pot, fund, betValue, initJackpotValue);
    }

    public short play(User user, String linesStr) throws Exception {

        String username = user.getName();

        Slot25FreeDailyMsg freeDailyMsg = new Slot25FreeDailyMsg(commandCollection.FREE_DAILY_MESSAGE);
        freeDailyMsg.remain = 0;


        Slot25ResultMsg msg = this.play(username, linesStr);

        if (this.isUserMinimize(user)) {
            Slot25MinimizeResultMsg miniMsg = new Slot25MinimizeResultMsg(commandCollection.MINIMIZE_RESULT_MESSAGE);
            miniMsg.prize = msg.prize;
            miniMsg.curretMoney = msg.currentMoney;
            miniMsg.result = msg.result;
            SlotUtils.sendMessageToUser(miniMsg, user);
        } else {
            SlotUtils.sendMessageToUser(msg, user);
            SlotUtils.sendMessageToUser(freeDailyMsg, user);
        }
        return msg.result;
    }

    @Override
    public Slot25ResultMsg playNormal(String username, String linesStr, long referenceId) {
        // kết quả mặc định
        short result = ResultSlot.MISSED;
        // thời điểm hiện tại
        String currentTimeStr = DateTimeUtils.getCurrentTime();
        // response message
        Slot25ResultMsg playResponse = new Slot25ResultMsg(commandCollection.RESULT_MESSAGE);
        // số line người chơi chọn
        String[] selectedLines = linesStr.split(",");
        // tổng cược
        long totalBetValue = (long) selectedLines.length * this.betValue;
        // từ PHP admin, cài đặt cho một người chơi trúng JACKPOT
        boolean forceJackpotToUser = false;
        // người chơi được set nổ hũ
        String usernameForce;
        // phòng được set nổ hũ
        String roomForce;
        // Lớp dịch vụ caching
        CacheServiceImpl cacheService = new CacheServiceImpl();
        // thông tin user
        UserCacheModel u = userService.getUser(username);
        // số dư hiện tại của user
        long currentMoney = userService.getMoneyUserCache(username, this.moneyTypeStr);
        // thông tin free spin
        int numOfFreeSpin = getNumOfFreeSpin(username);
        boolean isSpinningFree = numOfFreeSpin > 0;
        // số lines được chọn > 0
        if (selectedLines.length > 0 && !linesStr.isEmpty()) {
            // check tiền đặt cược hợp lệ
            if (totalBetValue > 0L) {
                // check đủ tiền
                if (totalBetValue <= currentMoney) {
                    // trừ phế theo số % được cấu hình
                    long fee = !isSpinningFree ? totalBetValue * percentFee / 100L : 0;
                    MoneyResponse moneyRes = new MoneyResponse(false, "1001");
                    // trừ tiền cược
                    // chỉ trừ tiền người chơi thật
                    if (!u.isBot()) {
                        long changeMoney = totalBetValue;
                        String desc = "Đặt cược " + gameName;
                        moneyRes = this.userService.updateMoney(username, -changeMoney, this.moneyTypeStr, this.gameName, "Quay " + gameName, desc, fee, referenceId, TransType.START_TRANS);
                    } else {
                        moneyRes.setSuccess(true);
                    }

                    if (moneyRes != null && moneyRes.isSuccess()) {
                        // số tiền còn lại sau khi trừ phế và 2% POT cho vào quỹ thưởng
                        long moneyToFund = !isSpinningFree ? totalBetValue - fee : 0;
                        if (!u.isBot() && moneyToFund > 0) {
                            updateFunValue(moneyToFund);
                        }
                        // cờ này được sử dụng để check liệu có tiếp tục vòng lặp để sinh Matrix hay không
                        boolean enoughPair = false;
                        // tổng tiền thắng được tính toán trên toàn bộ Lines được chọn
                        long totalPrizes;
                        // đếm số lượt xuất hiện Scatter
                        int countScatter;
                        // đếm số lượt bonus
                        int countBonus;
                        // Bonus Game Info model
                        MiniGameSlotResponse bonusGameResponse;
                        // danh sách giải thưởng được tính toán trên toàn bộ Lines được chọn
                        ArrayList<AwardsOnLine<Slot25ExtendAward>> awardsOnLines = new ArrayList<>();

                        synchronized (this) {
                            try {
                                usernameForce = cacheService.getValueStr(CACHE_NAME_USER_SPOT + gameName);
                                roomForce = cacheService.getValueStr(CACHE_BET_VALUE_SLOT + gameName);
                            } catch (Exception e) {
                                usernameForce = "";
                                roomForce = "";
                            }
                            // 1 phần trăm cho vào hũ JACKPOT
                            long moneyToPot = !isSpinningFree ? totalBetValue / 100L : 0;
                            this.pot += moneyToPot;
                            // BẮT ĐẦU QUÁ TRÌNH SINH MA TRẬN KẾT QUẢ VÀ TÍNH TOÁN GIẢI THƯỞNG
                            block4:
                            while (!enoughPair) {
                                // khởi tạo lại các giá trị mặc định sau mỗi lần lặp
                                result = ResultSlot.MISSED;
                                awardsOnLines.clear();
                                totalPrizes = 0L;
                                bonusGameResponse = null;
                                countScatter = 0;
                                countBonus = 0;
                                boolean isForceJackpot = false;

                                // xử lý FORCE nổ hũ
                                if (betValue == 100) {
                                    if ((usernameForce.equals(username) && roomForce.equals(String.valueOf(100)))) {
                                        isForceJackpot = true;
                                        forceJackpotToUser = true;
                                        result = ResultSlot.JACKPOT;
                                    }
                                } else if (betValue == 1000) {
                                    if ((usernameForce.equals(username) && roomForce.equals(String.valueOf(1000)))) {
                                        isForceJackpot = true;
                                        forceJackpotToUser = true;
                                        result = ResultSlot.JACKPOT;
                                    }
                                } else {
                                    if ((usernameForce.equals(username) && roomForce.equals(String.valueOf(10000)))) {
                                        isForceJackpot = true;
                                        forceJackpotToUser = true;
                                        result = ResultSlot.JACKPOT;
                                    }
                                }


                                // sinh Matrix
                                Slot25ExtendItem[][] matrix = isForceJackpot
                                        ? Slot25ExtendUtil.generateMatrixNoHu(selectedLines)
                                        : Slot25ExtendUtil.generateMatrix();

                                // MÃ LỆNH NÀY ÁP ỤNG CHO SLOT MACHINE 25LINE EXTENDS.
                                // Trường hợp 1 WHEEL có xuất hiện item WILD, toàn bộ WHEEL đó sẽ được thay thế bởi nó
                                // Trong trường hợp này (Slot Machine 25Line Basic thì không áp dụng)
                                Slot25ExtendItem[][] matrixWild = Slot25ExtendUtil.revertMatrix(matrix);

                                // Đếm số lượng BONUS và SCATTER
                                for (int i = 0; i < ROW; ++i) {
                                    for (int j = 0; j < COLUMN; ++j) {
                                        if (matrix[i][j] == Slot25ExtendItem.SCATTER) {
                                            ++countScatter;
                                            continue;
                                        }
                                        if (matrix[i][j] == Slot25ExtendItem.BONUS) {
                                            ++countBonus;
                                        }
                                    }
                                }


                                // không cho phép JACKPOT và (BONUS hoặc FREE SPIN) xảy ra đồng thời
                                if (isForceJackpot && (countBonus >= 3 || countScatter >= 4)) {
                                    continue;
                                }


                                // không cho phép BONUS và FREE SPIN xảy ra đồng thời
                                if (countBonus >= 3 && countScatter >= 4) {
                                    continue;
                                }

                                // ở chế độ Free Spin, không cho phép trúng BONUS hoặc SCATTER
                                if (isSpinningFree && (countScatter >= 3 || countBonus >= 3)) {
                                    continue;
                                }

                                // Tính toán phần thưởng cho BONUS GAME
                                if (countBonus >= 3) {
                                    bonusGameResponse = Slot25ExtendUtil.buildBonusGameData(this.betValue, countBonus);
                                    Slot25ExtendAward bonusAward = Slot25ExtendAwards.getAward(Slot25ExtendItem.BONUS, countBonus);
                                    AwardsOnLine<Slot25ExtendAward> aol = new AwardsOnLine<>(bonusAward, bonusGameResponse.getTotalPrize(), "line0");
                                    awardsOnLines.add(aol);
                                    result = ResultSlot.BONUS_GAME;
                                }

                                // Duyệt toàn bộ Lines được chọn bởi người chơi để tính toán giải thưởng trên từng Line
                                for (String selectedLine : selectedLines) {
                                    ArrayList<Slot25ExtendAward> awardList = new ArrayList<>();
                                    int lineNumber = Integer.parseInt(selectedLine);
                                    Line line = Slot25ExtendUtil.getLine(this.lines, matrixWild, lineNumber);
                                    Slot25ExtendUtil.calculateMoneyAwardInLine(line, awardList);
                                    for (Slot25ExtendAward award : awardList) {
                                        long moneyOnLine;
                                        if (award == Slot25ExtendAward.PENTA_JACKPOT) {
                                            // đảm bảo chỉ duy nhất 1 dòng trúng JACKPOT
                                            // nếu trùng lặp, bắt đầu lại dòng vòng lặp while (continue block4)
                                            for (AwardsOnLine e : awardsOnLines) {
                                                if (e.getAward() != Slot25ExtendAward.PENTA_JACKPOT) {
                                                    continue;
                                                }
                                                continue block4;
                                            }
                                            moneyOnLine = this.pot;
                                            result = ResultSlot.JACKPOT;
                                        } else {
                                            moneyOnLine = (long) (award.getRatio() * this.betValue);
                                        }
                                        AwardsOnLine<Slot25ExtendAward> aol2 = new AwardsOnLine<>(award, moneyOnLine, line.getName());
                                        awardsOnLines.add(aol2);
                                    }
                                }


                                // Tiếp theo, tính toán toàn bộ giải thưởng
                                boolean isGetJackpotNaturally = false;
                                StringBuilder builderLinesWin = new StringBuilder();
                                StringBuilder builderPrizesOnLine = new StringBuilder();
                                for (AwardsOnLine<Slot25ExtendAward> award : awardsOnLines) {
                                    totalPrizes += award.getMoney();

                                    builderLinesWin.append(",");
                                    builderLinesWin.append(award.getLineId());

                                    builderPrizesOnLine.append(",");
                                    builderPrizesOnLine.append(award.getMoney());

                                    if (!isForceJackpot && award.getAward() == Slot25ExtendAward.PENTA_JACKPOT) {
                                        result = ResultSlot.JACKPOT;
                                        isGetJackpotNaturally = true;
                                    }
                                }

                                if (builderLinesWin.length() > 0) {
                                    builderLinesWin.deleteCharAt(0);
                                }
                                if (builderPrizesOnLine.length() > 0) {
                                    builderPrizesOnLine.deleteCharAt(0);
                                }


                                // Kiểm tra xem giải thưởng có LỚN hay không.
                                // Lớn quá thì sinh lại MATRIX kết quả kẻo anh em NPH vỡ nợ
                                if (!isForceJackpot) {
                                    // Trúng nổ hũ một cách ngẫu nhiên nhưng qũy thưởng lại không đủ bù lỗ
                                    if (isGetJackpotNaturally && getFunValue() < initJackpotValues) {
                                        continue;
                                    }
                                    // Tuy không trúng JACKPOT nhưng trúng Line to quá cũng cần sinh lại MATRIX
                                    if (!isGetJackpotNaturally) {
                                        if (totalPrizes > 0 && totalPrizes > getFunValue() && !u.isBot())
                                            continue;
                                    }
                                }


                                // điều kiện trúng thưởng đã thỏa mãn, dừng vòng lặp
                                enoughPair = true;
                                // BẮT ĐẦU QUÁ TRÌNH LƯU TRỮ THÔNG TIN VÀ TRẢ THƯỞNG
                                String matrixStr = Slot25ExtendUtil.matrixToString(matrix);
                                if (totalPrizes > 0L) {

                                    if (!u.isBot()) {
                                        updateFunValue(-totalPrizes);
                                    }

                                    if (result == ResultSlot.JACKPOT) {
                                        this.pot = this.initJackpotValues;
                                        // get user cache
                                        String displayName = username;
                                        if (forceJackpotToUser) {
                                            try {
                                                cacheService.removeKey(CACHE_NAME_USER_SPOT + gameName);
                                                cacheService.removeKey(CACHE_BET_VALUE_SLOT + gameName);
                                            } catch (Exception e) {
                                                logger.error(" Reset cache " + gameName + " error with : " + e.getMessage());
                                            }
                                        }
                                        this.slotService.logNoHu(referenceId, this.gameName, displayName, this.betValue, linesStr, matrixStr, builderLinesWin.toString(), builderPrizesOnLine.toString(), totalPrizes, result, currentTimeStr);
                                    } else {
                                        if (result == ResultSlot.MISSED) {
                                            result = totalPrizes >= (this.betValue * 175L) ? ResultSlot.BIG_WIN : ResultSlot.WIN;
                                        }
                                    }
                                }


                                // cập nhật và tính toán lượt quay miễn phí
                                SlotFreeSpin slotFreeSpin = slotService.updateLuotQuaySlotFree(cacheFreeSpinName, username);
                                playResponse.freeSpin = (byte) this.setFreeSpin(username, linesStr, countScatter, slotFreeSpin.getNum());
                                if (countScatter >= 3) {
                                    playResponse.isFreeSpin = true;
                                    if (result != ResultSlot.BONUS_GAME && result != ResultSlot.JACKPOT) {
                                        result = ResultSlot.FREE_SPIN;
                                    }
                                }


                                // only save real user
                                long moneyExchange = totalPrizes;
                                if (moneyExchange != 0 && !u.isBot()) {
                                    moneyRes = this.userService.updateMoney(username, moneyExchange, this.moneyTypeStr, this.gameName, "Quay " + gameName, this.buildDescription(totalBetValue, totalPrizes, result), 0L, referenceId, TransType.END_TRANS);
                                    if (moneyRes != null && moneyRes.isSuccess()) {
                                        currentMoney = moneyRes.getCurrentMoney();
                                    }
                                }

                                if (moneyRes != null && moneyRes.isSuccess()) {
                                    // thông báo tới toàn hệ thống số tiền thắng của người chơi
                                    if (this.moneyType == 1 && moneyExchange - this.betValue >= (totalBetValue * 1.5)) {
                                        this.broadcastMsgService.putMessage(Games.findGameByName(gameName).getId(), username, moneyExchange - (long) this.betValue);
                                    }
                                }

                                String linesWin = builderLinesWin.toString();
                                String prizesOnLine = builderPrizesOnLine.toString();
                                playResponse.referenceId = referenceId;
                                playResponse.matrix = matrixStr;
                                playResponse.linesWin = linesWin;
                                playResponse.prize = totalPrizes;
                                if (bonusGameResponse != null) {
                                    playResponse.haiSao = bonusGameResponse.getPrizes();
                                }
                                try {
                                    // lưu nhật ký chơi
                                    if (!u.isBot()) {
                                        this.logListener.log(referenceId, username, this.betValue, linesStr, linesWin, prizesOnLine, result, totalPrizes, currentTimeStr, matrixStr);
                                    }
                                    // lưu nhật ký nổ hũ
                                    if (result == ResultSlot.JACKPOT) {
                                        this.slotService.addTop(gameName, username, this.betValue, totalPrizes, currentTimeStr, result);
                                    }
                                    // thông báo tới toàn bộ người chơi trong game (trong MODULE)
                                    if (result == ResultSlot.JACKPOT || result == ResultSlot.BIG_WIN) {
                                        Slot25BigWinMsg bigWinMsg = new Slot25BigWinMsg(commandCollection.BIG_WIN_MESSAGE);
                                        bigWinMsg.username = username;
                                        bigWinMsg.type = (byte) result;
                                        bigWinMsg.betValue = (short) this.betValue;
                                        bigWinMsg.totalPrizes = totalPrizes;
                                        bigWinMsg.timestamp = DateTimeUtils.getCurrentTime();
                                        this.module.sendMsgToAllUsers(bigWinMsg);
                                    }
                                } catch (InterruptedException | TimeoutException | IOException ignored) {
                                    // empty catch block
                                }
                                // lưu thông tin quỹ
                                this.saveFund();
                                // lưu thông tin HŨ
                                this.savePot();
                            }
                        }
                    }
                } else {
                    result = ResultSlot.NOT_ENOUGH_MONEY;
                }
            } else {
                result = ResultSlot.INVALID_BET_VALUE;
            }
        } else {
            result = ResultSlot.INVALID_BET_VALUE;
        }
        playResponse.result = (byte) result;
        playResponse.currentMoney = currentMoney;

        // update cache tien hu
        cacheService.setValue(CACHE_JACK_POT_VALUE_SLOT + "_" + this.betValue + "_" + gameName, String.valueOf(this.pot));
        if (result == ResultSlot.JACKPOT) {
            this.sendNotifyNoHu(username, (byte) 1, playResponse.prize, gameName);
        }
        return playResponse;
    }

    protected int setFreeSpin(String nickName, String lines, int countFreeSpin, int remain) {
        int soLuot = 0;
        switch (countFreeSpin) {
            case 3: {
                soLuot = 3 + remain;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 1, betValue);
                return 3;
            }
            case 4: {
                soLuot = 6 + remain;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 1, betValue);
                return 6;
            }
            case 5: {
                soLuot = 18 + remain;
                slotService.setLuotQuayFreeSlot(this.cacheFreeSpinName, nickName, lines, soLuot, 2, betValue);
                return 18;
            }
        }
        return Math.max(soLuot, remain);
    }

}
