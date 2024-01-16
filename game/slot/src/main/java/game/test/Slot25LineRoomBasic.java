
package game.test;


import com.google.gson.Gson;
import com.vinplay.dal.service.impl.CacheServiceImpl;
import com.vinplay.vbee.common.response.MoneyResponse;
import com.vinplay.vbee.common.utils.DateTimeUtils;
import game.modules.slot.cmd.send.benley.ResultBenleyMsg;
import game.modules.slot.entities.slot.AwardsOnLine;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.avengers.Line25Award;
import game.modules.slot.entities.slot.avengers.Line25AwardManager;
import game.modules.slot.entities.slot.avengers.Line25Item;
import game.modules.slot.entities.slot.avengers.Line25Lines;
import game.modules.slot.utils.Line25Utils;

import java.util.ArrayList;
import java.util.Random;

public class Slot25LineRoomBasic {

    public static void main(String[] args) {
        long fund = 0;
        long initJackpotValues = 5000000;
        long pot = initJackpotValues;
        int betValue = 1000;
        long currentMoney = 100000000;
        long totalFee = 0;
        long referenceId = 0;

        final Line25Lines lines = new Line25Lines();

        String linesStr = "1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25";
        for (int numberSpin = 0; numberSpin < 100; numberSpin++) {
            short result = ResultSlot.MISSED;
            String currentTimeStr = DateTimeUtils.getCurrentTime();
            ResultBenleyMsg resultBenleyMsg = new ResultBenleyMsg();

            String[] selectedLines = linesStr.split(",");
            long totalBetValue = (long) selectedLines.length * betValue;

            boolean forceJackpotToUser = false;
            MiniGameSlotResponse bonusGameResponse = null;
            
            Random random = new Random();
            if (random.nextInt(2) == 1) {
                forceJackpotToUser = true;
            }
            long totalPrizes = 0;
            // get force user jackpot
            CacheServiceImpl cacheService = new CacheServiceImpl();
            // số lines được chọn > 0
            if (selectedLines.length > 0 && !linesStr.isEmpty()) {
                // check tiền đặt cược hợp lệ
                if (totalBetValue > 0L) {
                    // check đủ tiền
                    if (totalBetValue <= currentMoney) {
                        // trừ phế 2%
                        long fee = totalBetValue * 2L / 100L;
                        totalFee += fee;
                        MoneyResponse moneyRes = new MoneyResponse(false, "1001");
                        // Không phải lả BOT => Cập nhật tiền
                        currentMoney -= totalBetValue;
                        moneyRes.setSuccess(true);
                        
                        if (moneyRes != null && moneyRes.isSuccess()) {
                            // một phần trăm cho vào hũ JACKPOT
                            long moneyToPot = totalBetValue * 2 / 100L;
                            pot += moneyToPot;

                            // số tiền còn lại sau khi trừ phế và 1% POT cho vào quỹ thưởng
                            long moneyToFund = totalBetValue - fee - moneyToPot;
                            fund += moneyToFund;

                            // cờ này được sử dụng để check liệu có tiếp tục vòng lặp để sinh Matrix hay không
                            boolean enoughPair = false;


                            int countScatter;
                            int countBonus;

                            
                            ArrayList<AwardsOnLine<Line25Award>> awardsOnLines = new ArrayList<>();
                            while (!enoughPair) {
                                result = ResultSlot.MISSED;
                                awardsOnLines.clear();
                                totalPrizes = 0L;
                                String linesWin;
                                String prizesOnLine;
                                bonusGameResponse = null;
                                countScatter = 0;
                                countBonus = 0;
                                boolean isForceJackpot = false;

                                // sinh Matrix
                                Line25Item[][] matrix = isForceJackpot ? Line25Utils.generateMatrixNoHu(selectedLines) : Line25Utils.generateMatrix();

                                // Đếm số lượng BONUS và SCATTER
                                for (int i = 0; i < 3; ++i) {
                                    // cột
                                    for (int j = 0; j < 5; ++j) {
                                        if (matrix[i][j] == Line25Item.SCATTER) {
                                            ++countScatter;
                                            continue;
                                        }
                                        if (matrix[i][j] == Line25Item.BONUS) {
                                            ++countBonus;
                                        }
                                    }
                                }

                                // không cho phép nổ hũ và (BONUS hoặc FREE spin) xảy ra đồng thời
                                if (isForceJackpot && (countBonus >= 3 || countScatter >= 3)) {
                                    continue;
                                }
                                // không cho phép BONUS và FREE spin xảy ra đồng thời
                                if (countBonus >= 3 && countScatter >= 3) {
                                    continue;
                                }
                                // trong trường hợp thỏa mãn BONUS VÀ SCATTER,
                                // ràng buộc thêm điều kiện để giảm tỷ lệ ăn BONUS và SCATTER xuống
                                // nếu không thỏa mãn điều kiện, tiếp tục vòng lặp để sinh lại Matrix
//                                if (countBonus >= 3 || countScatter >= 3) {
//                                    int tiLeAn = selectedLines.length * 100 / 25;
//                                    Random rd2 = new Random();
//                                    int n2 = rd2.nextInt(100);
//                                    if (n2 >= tiLeAn)
//                                        continue;
//                                }

                                // Tính toán phần thưởng cho BONUS GAME
                                if (countBonus >= 3) {
                                    bonusGameResponse = Line25Utils.buildBonusGameData(betValue, countBonus);
                                    Line25Award bonusAward = Line25AwardManager.getAward(Line25Item.BONUS, countBonus);
                                    AwardsOnLine<Line25Award> aol = new AwardsOnLine<>(bonusAward, bonusGameResponse.getTotalPrize(), "line0");
                                    awardsOnLines.add(aol);
                                    result = ResultSlot.BONUS_GAME;
                                }

                                // MÃ LỆNH NÀY ÁP ỤNG CHO SLOT MACHINE 25LINE EXTENDS.
                                // Trường hợp 1 WHEEL có xuất hiện item WILD, toàn bộ WHEEL đó sẽ được thay thế bởi nó
                                // Trong trường hợp này (Slot Machine 25Line Basic thì không áp dụng)
                                /* AvengersItem[][] matrixWild = AvengersUtils.revertMatrix(matrix); */

                                Line25Item[][] matrixWild = matrix;

                                // Duyệt toàn bộ Lines được chọn bởi người chơi để tính toán giải thưởng trên từng Line
                                for (String selectedLine : selectedLines) {
                                    ArrayList<Line25Award> awardList = new ArrayList<>();
                                    int lineNumber = Integer.parseInt(selectedLine);
                                    Line line = Line25Utils.getLine(lines, matrixWild, lineNumber);
                                    Line25Utils.calculateMoneyAwardInLine(line, awardList);
                                    for (Line25Award award : awardList) {
                                        long moneyOnLine = (long) (award.getRatio() * betValue);
                                        AwardsOnLine<Line25Award> aol2 = new AwardsOnLine<>(award, moneyOnLine, line.getName());
                                        awardsOnLines.add(aol2);
                                    }
                                }

                                // Tiếp theo, tính toán toàn bộ giải thưởng
                                boolean isGetJackpotNaturally = false;
                                StringBuilder builderLinesWin = new StringBuilder();
                                StringBuilder builderPrizesOnLine = new StringBuilder();
                                for (AwardsOnLine<Line25Award> award : awardsOnLines) {
                                    totalPrizes += award.getMoney();

                                    builderLinesWin.append(",");
                                    builderLinesWin.append(award.getLineId());

                                    builderPrizesOnLine.append(",");
                                    builderPrizesOnLine.append(award.getMoney());

                                    if (result != ResultSlot.JACKPOT && award.getAward() == Line25Award.PENTA_JACKPOT) {
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
                                    if (isGetJackpotNaturally && fund < initJackpotValues) {
                                        continue;
                                    }
                                    // Tuy không trúng JACKPOT nhưng trúng Line to quá cũng cần sinh lại MATRIX
                                    if (!isGetJackpotNaturally) {
                                        if ((totalPrizes - totalBetValue > 0 && totalPrizes > fund) || totalPrizes >= totalBetValue * 20)
                                            continue;
                                    }
                                }

                                enoughPair = true;
                                String matrixStr = Line25Utils.matrixToString(matrix);
                                if (totalPrizes > 0L) {
                                    if (result == ResultSlot.JACKPOT) {
                                        pot = initJackpotValues;
                                        fund -= initJackpotValues;
                                    } else {
                                        fund -= totalPrizes;
                                        if (result == ResultSlot.MISSED) {
                                            result = totalPrizes >= (totalBetValue * 155) ? ResultSlot.BIG_WIN : ResultSlot.WIN;
                                        }
                                    }
                                }

                                // tính toán lượt quay miễn phí
                                resultBenleyMsg.freeSpin = (byte) setFreeSpin(countScatter);
                                long moneyExchange = totalPrizes;

                                // only save real user
                                if (moneyExchange != 0) {
                                    currentMoney += moneyExchange;
                                }
                                linesWin = builderLinesWin.toString();
                                prizesOnLine = builderPrizesOnLine.toString();
                                resultBenleyMsg.referenceId = referenceId;
                                resultBenleyMsg.matrix = Line25Utils.matrixToString(matrix);
                                resultBenleyMsg.linesWin = linesWin;
                                resultBenleyMsg.prize = totalPrizes;
                                resultBenleyMsg.isFreeSpin = false;
                                if (bonusGameResponse != null) {
                                    resultBenleyMsg.haiSao = bonusGameResponse.getPrizes();
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
            resultBenleyMsg.result = (byte) result;
            resultBenleyMsg.currentMoney = currentMoney;
            String specialAward = resultToString(result);
            if(result == ResultSlot.BONUS_GAME) {
                System.out.println(new Gson().toJson(bonusGameResponse));
            }
            System.out.println("JACKPOT: " + pot
                    + " | FUND: " + fund
                    + " | FEE:" + totalFee
                    + " | CURRENT MONEY: " + currentMoney
                    + " | PRIZE: " + totalPrizes
                    + " | WIN/LOSE: " + (totalPrizes - totalBetValue)
                    + " | " + resultToString(result)
                    + " | FREE SPIN: " + resultBenleyMsg.freeSpin);
        }


    }

    private static int setFreeSpin(int countFreeSpin) {
        int soLuot = 0;
        switch (countFreeSpin) {
            case 3: {
                soLuot = 4;
                break;
            }
            case 4: {
                soLuot = 8;
                break;
            }
            case 5: {
                soLuot = 22;
            }
        }
        return soLuot;
    }

    protected static String resultToString(short result) {
        switch (result) {
            case 1: {
                return "Thắng";
            }
            case 2: {
                return "Thắng lớn";
            }
            case 3: {
                return "Nổ hũ";
            }
            case 4: {
                return "Nổ hũ X2";
            }
            case 5: {
                return "Bonus";
            }
            default:
                return "Trượt";
        }
    }

    public static class ResultSlot {
        public static final short SYSTEM_ERROR = 100;
        public static final short INVALID_BET_VALUE = 101;
        public static final short NOT_ENOUGH_MONEY = 102;
        public static final short INVALID_FREE_SPIN = 103;
        public static final short MISSED = 0;
        public static final short WIN = 1;
        public static final short BIG_WIN = 2;
        public static final short JACKPOT = 3;
        public static final short JACKPOT_X2 = 4;
        public static final short BONUS_GAME = 5;
    }
}


