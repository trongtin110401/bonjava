/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.utils;

import com.vinplay.vbee.common.utils.NumberUtils;
import game.modules.slot.entities.slot.Cell;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.avengers.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AvengersUtils {

    public static AvengersItem[][] generateMatrix() {
        AvengersItems items = new AvengersItems();
        AvengersItem[][] matrix = new AvengersItem[3][5];
        Random rd = new Random();
        int n = rd.nextInt(3);
        for (int row = 0; row < 3; ++row) {
            int r = n + row;
            if (r > 2) {
                r -= 3;
            }
            for (int column = 0; column < 5; ++column) {
                boolean isContinueGenerate = true;
                AvengersItem item = null;
                while (isContinueGenerate) {
                    isContinueGenerate = false;
                    item = items.random(column);
                    if (!AvengersUtils.isSpecialItem(item)) {
                        continue;
                    }
                    if (item == AvengersItem.WILD) {
                        if (!AvengersUtils.isSpecialItem(matrix[0][column])
                                && !AvengersUtils.isSpecialItem(matrix[1][column])
                                && !AvengersUtils.isSpecialItem(matrix[2][column])) {
                            continue;
                        }
                        isContinueGenerate = true;
                        items.refundItem(item, column);
                        continue;
                    }
                    if (matrix[0][column] != item
                            && matrix[1][column] != item
                            && matrix[2][column] != item
                            && matrix[0][column] != AvengersItem.WILD
                            && matrix[1][column] != AvengersItem.WILD
                            && matrix[2][column] != AvengersItem.WILD) {
                        continue;
                    }
                    isContinueGenerate = true;
                    items.refundItem(item, column);
                }
                matrix[r][column] = item;
            }
        }
        return matrix;
    }

    public static boolean isSpecialItem(AvengersItem item) {
        return item == AvengersItem.BONUS || item == AvengersItem.SCATTER || item == AvengersItem.JACKPOT || item == AvengersItem.WILD;
    }

    public static AvengersItem[][] generateMatrixFreeSpin(String itemsWild) {
        int i;
        String[] arr = itemsWild.split(",");
        AvengersFreeSpinItems items = new AvengersFreeSpinItems();
        AvengersItem[][] matrix = new AvengersItem[3][5];
        if (arr.length > 0) {
            for (i = 0; i < arr.length - 1; i += 2) {
                int r = Integer.parseInt(arr[i]);
                int c = Integer.parseInt(arr[i + 1]);
                matrix[r][c] = AvengersItem.WILD;
            }
        }
        for (i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (matrix[i][j] != null) continue;
                matrix[i][j] = items.random(j);
            }
        }
        return matrix;
    }

    public static AvengersItem[][] generateMatrixNoHu(String[] lineArr) {
        AvengersItem[][] matrix = new AvengersItem[3][5];
        Random rd = new Random();
        int n = rd.nextInt(lineArr.length);
        int indexLineNoHu = Integer.parseInt(lineArr[n]) - 1;
        AvengersLines lines = new AvengersLines();
        AvengersItems items = new AvengersItems();
        Line<AvengersItem> lineNoHu = lines.get(indexLineNoHu);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                boolean genRandom = true;
                for (int k = 0; k < lineNoHu.getCells().size(); ++k) {
                    if (i != lineNoHu.getCell(k).getRow() || j != lineNoHu.getCell(k).getCol()) continue;
                    genRandom = false;
                    matrix[i][j] = AvengersItem.JACKPOT;
                }
                if (!genRandom) continue;
                AvengersItem item = AvengersItem.JACKPOT;
                while (item == AvengersItem.JACKPOT || item == AvengersItem.WILD) {
                    item = items.random(j);
                }
                matrix[i][j] = item;
            }
        }
        return matrix;
    }

    public static String matrixToString(AvengersItem[][] matrix) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                builder.append(",");
                builder.append(matrix[i][j].getId());
            }
        }
        // xóa ký tự "," đầu tiên
        if (builder.length() > 0) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    public static Line getLine(AvengersLines lines, AvengersItem[][] matrix, int lineIndex) {
        Line<AvengersItem> line = lines.get(lineIndex - 1);
        for (Cell<AvengersItem> cell : line.getCells()) {
            AvengersItem itemInMatrix = matrix[cell.getRow()][cell.getCol()];
            cell.setItem(itemInMatrix);
        }
        return line;
    }

    public static MiniGameSlotResponse buildBonusGameData(int betValue, int countBonus) {
        Random rd = new Random();
        int indexRatioCol = rd.nextInt(3);
        int indexRatioRow = countBonus - 3;
        int ratio = Constant.AVENGERS_BONUS_RATIO[indexRatioRow][indexRatioCol];
        MiniGameSlotResponse res = AvengersUtils.generateMiniGameSlot(betValue);
        res.setTotalPrize(res.getTotalPrize() * (long) ratio);
        res.setPrizes(res.getPrizes() + "," + ratio + "," + countBonus);
        return res;
    }

    private static MiniGameSlotResponse generateMiniGameSlot(int baseBetting) {
        MiniGameSlotResponse response = new MiniGameSlotResponse();
        int step = 0;
        long tongGiai = 0L;
        boolean chonTiep;
        Random rd = new Random();
        StringBuilder sb = new StringBuilder();
        do {
            if (rd.nextInt(100) + 1 > Constant.AVENGERS_TANK_TI_LE_TRUOT[step]) {
                int indexCol = rd.nextInt(15);
                int prize = Constant.AVENGERS_TANK_PRIZES[step][indexCol] * baseBetting;
                sb.append(prize);
                sb.append(",");
                tongGiai += prize;
                chonTiep = true;
            } else {
                chonTiep = false;
            }
            ++step;
        } while (chonTiep);

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }

        response.setPrizes(sb.toString());
        response.setTotalPrize(tongGiai);
        return response;
    }

    public static void main(String[] args) {
//        int n = 1000;
//        long total = 0L;
//        for (int i = 0; i < 1000; ++i) {
//            MiniGameSlotResponse res = AvengersUtils.generateMiniGameSlot(1000);
//            total += res.getTotalPrize();
//            System.out.println(res.getTotalPrize());
//        }
//        System.out.println("Trung binh: " + total / 1000L);


        System.out.println(matrixToString(generateMatrix()));
    }

    public static void calculateAward(Line line, List<AvengersAward> awardList) {
        int countNumItems = 0;
        AvengersItem firstLineItem = (AvengersItem) line.getCell(0).getItem();
        if (firstLineItem != AvengersItem.BONUS && firstLineItem != AvengersItem.SCATTER) {
            for (int i = 0; i < line.getCells().size(); ++i) {
                byte itemId = ((AvengersItem) line.getCell(i).getItem()).getId();
                if (itemId == firstLineItem.getId()
                        || firstLineItem.getId() != AvengersItem.JACKPOT.getId()
                        && itemId == AvengersItem.WILD.getId()) {
                    ++countNumItems;
                }
            }

            AvengersAward award;
            if (countNumItems >= 3 && (award = AvengersAwardManager.getAward(firstLineItem, countNumItems)) != null) {
                awardList.add(award);
            }
        }
    }

    /**
     * Phương thức này được sử dụng để tính toán giải thưởng cho số lần xuất hiện của ITEM trên 1 LINE
     * mà không bao gồm việc tính toán số lần quay miễn phí và giải thưởng cho BONUS game
     *
     * @param line
     * @param awardList
     */
    public static void calculateMoneyAwardInLine(Line line, List<AvengersAward> awardList) {
        // ánh xạ giữa item và số lượng xuất hiện của nó trên 1 Line
        Map<Byte, Integer> itemId2Count = new HashMap<>();
        // duyệt qua các cell trên 1 line để tính toán số lần xuất hiện
        for (int cellIndex = 0; cellIndex < line.getCells().size(); cellIndex++) {
            Cell cell = line.getCell(cellIndex);
            AvengersItem avengersItem = (AvengersItem) cell.getItem();
            Integer countNumberItem = itemId2Count.get(avengersItem.getId());
            if (countNumberItem == null) {
                countNumberItem = 1;
            } else {
                countNumberItem += 1;
            }
            itemId2Count.put(avengersItem.getId(), countNumberItem);
        }
        // bắt đầu tính toán giải thưởng đạt được trên 1 line
        itemId2Count.forEach((id, countNumItem) -> {
            // Chỉ có item có số lần xuất hiện lớn hơn hoặc bằng 2 thì mới tính toán giải thưởng
            if (countNumItem >= 2) {
                AvengersItem item = AvengersItem.findItem(id);
                // Bởi vì BONUS và SCATTER không có giải thưởng tiền trên 1 LINE
                // nên ta có thể bỏ qua mà không cần tính toán
                if (item != AvengersItem.BONUS && item != AvengersItem.SCATTER) {
                    AvengersAward award = AvengersAwardManager.getAward(item, countNumItem);
                    if (award != null) {
                        awardList.add(award);
                    }
                }
            }
        });
    }

    public static void calculateFreeSpinLine(Line line, List<AvengersFreeSpinAward> awardList) {
        int countNumItems = 0;
        AvengersItem itemSample = (AvengersItem) line.getCell(0).getItem();
        if (itemSample != AvengersItem.BONUS && itemSample != AvengersItem.SCATTER && itemSample != AvengersItem.JACKPOT) {
            AvengersFreeSpinAward award;
            for (int j = 0; j < line.getCells().size() && (line.getCell(j).getItem() == itemSample || line.getCell(j).getItem() == AvengersItem.WILD); ++j) {
                ++countNumItems;
            }
            if (countNumItems >= 3 && (award = AvengersFreeSpinAwardManager.getAward(itemSample, countNumItems)) != null) {
                awardList.add(award);
            }
        }
    }

    public static AvengersItem[][] revertMatrix(AvengersItem[][] m) {
        AvengersItem[][] matrix = new AvengersItem[3][5];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (matrix[i][j] != null) continue;

                matrix[i][j] = m[i][j];
                if (matrix[i][j] != AvengersItem.WILD) continue;

                matrix[0][j] = AvengersItem.WILD;
                matrix[1][j] = AvengersItem.WILD;
                matrix[2][j] = AvengersItem.WILD;
            }
        }
        return matrix;
    }
}

