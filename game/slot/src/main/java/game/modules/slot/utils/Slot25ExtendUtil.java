/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.utils;

import game.modules.slot.entities.slot.Cell;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.line25extend.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Slot25ExtendUtil {

    public static Slot25ExtendItem[][] generateMatrix() {
        Slot25ExtendItems items = new Slot25ExtendItems();
        Slot25ExtendItem[][] matrix = new Slot25ExtendItem[3][5];
        Random rd = new Random();
        int n = rd.nextInt(3);
        for (int row = 0; row < 3; ++row) {
            int r = n + row;
            if (r > 2) {
                r -= 3;
            }
            for (int column = 0; column < 5; ++column) {
                boolean isContinueGenerate = true;
                Slot25ExtendItem item = null;
                while (isContinueGenerate) {
                    isContinueGenerate = false;
                    item = items.random(column);
                    if (!Slot25ExtendUtil.isSpecialItem(item)) {
                        continue;
                    }
                    if (item == Slot25ExtendItem.WILD) {
                        if (!Slot25ExtendUtil.isSpecialItem(matrix[0][column])
                                && !Slot25ExtendUtil.isSpecialItem(matrix[1][column])
                                && !Slot25ExtendUtil.isSpecialItem(matrix[2][column])) {
                            continue;
                        }
                        isContinueGenerate = true;
                        items.refundItem(item, column);
                        continue;
                    }
                    if (matrix[0][column] != item
                            && matrix[1][column] != item
                            && matrix[2][column] != item
                            && matrix[0][column] != Slot25ExtendItem.WILD
                            && matrix[1][column] != Slot25ExtendItem.WILD
                            && matrix[2][column] != Slot25ExtendItem.WILD) {
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

    public static boolean isSpecialItem(Slot25ExtendItem item) {
        return item == Slot25ExtendItem.BONUS || item == Slot25ExtendItem.SCATTER || item == Slot25ExtendItem.JACKPOT || item == Slot25ExtendItem.WILD;
    }

    public static Slot25ExtendFreeSpinItem[][] generateMatrixFreeSpin(String itemsWild) {
        int row;
        String[] arr = itemsWild.split(",");
        Slot25ExtendFreeSpinItems items = new Slot25ExtendFreeSpinItems();
        Slot25ExtendFreeSpinItem[][] matrix = new Slot25ExtendFreeSpinItem[3][5];
        if (arr.length > 0) {
            for (row = 0; row < arr.length - 1; row += 2) {
                int r = Integer.parseInt(arr[row]);
                int c = Integer.parseInt(arr[row + 1]);
                matrix[r][c] = Slot25ExtendFreeSpinItem.WILD;
            }
        }
        for (row = 0; row < 3; ++row) {
            for (int col = 0; col < 5; ++col) {
                if (matrix[row][col] != null) continue;
                matrix[row][col] = items.random(col);
            }
        }
        return matrix;
    }

    public static Slot25ExtendItem[][] generateMatrixNoHu(String[] lineArr) {
        Slot25ExtendItem[][] matrix = new Slot25ExtendItem[3][5];
        Random rd = new Random();
        int n = rd.nextInt(lineArr.length);
        int indexLineNoHu = Integer.parseInt(lineArr[n]) - 1;
        Slot25ExtendLines lines = new Slot25ExtendLines();
        Slot25ExtendItems items = new Slot25ExtendItems();
        Line<Slot25ExtendItem> lineNoHu = lines.get(indexLineNoHu);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                boolean genRandom = true;
                for (int k = 0; k < lineNoHu.getCells().size(); ++k) {
                    if (i != lineNoHu.getCell(k).getRow() || j != lineNoHu.getCell(k).getCol()) continue;
                    genRandom = false;
                    matrix[i][j] = Slot25ExtendItem.JACKPOT;
                }
                if (!genRandom) continue;
                Slot25ExtendItem item = Slot25ExtendItem.JACKPOT;
                while (item == Slot25ExtendItem.JACKPOT || item == Slot25ExtendItem.WILD) {
                    item = items.random(j);
                }
                matrix[i][j] = item;
            }
        }
        return matrix;
    }

    public static String matrixToString(Slot25ExtendItem[][] matrix) {
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

    public static String matrixToString(Slot25ExtendFreeSpinItem[][] matrix) {
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

    public static Line getLine(Slot25ExtendLines lines, Slot25ExtendItem[][] matrix, int lineIndex) {
        Line<Slot25ExtendItem> line = lines.get(lineIndex - 1);
        for (Cell<Slot25ExtendItem> cell : line.getCells()) {
            Slot25ExtendItem itemInMatrix = matrix[cell.getRow()][cell.getCol()];
            cell.setItem(itemInMatrix);
        }
        return line;
    }

    public static Line getLine(Slot25ExtendLines lines, Slot25ExtendFreeSpinItem[][] matrix, int lineIndex) {
        Line<Slot25ExtendFreeSpinItem> line = lines.get(lineIndex - 1);
        for (Cell<Slot25ExtendFreeSpinItem> cell : line.getCells()) {
            Slot25ExtendFreeSpinItem itemInMatrix = matrix[cell.getRow()][cell.getCol()];
            cell.setItem(itemInMatrix);
        }
        return line;
    }

    public static MiniGameSlotResponse buildBonusGameData(int betValue, int countBonus) {
        Random rd = new Random();
        int indexRatioCol = rd.nextInt(3);
        int indexRatioRow = countBonus - 3;
        int ratio = Constant.SLOT25_BONUS_RATIO[indexRatioRow][indexRatioCol];
        MiniGameSlotResponse res = Slot25ExtendUtil.generateMiniGameSlot(betValue);
        res.setTotalPrize(res.getTotalPrize() * (long) ratio);
        res.setPrizes(res.getPrizes() + "," + ratio + "," + countBonus);
        return res;
    }

    /**
     * Sinh dữ liệu game BONUS
     *
     * @param baseBetting
     * @return
     */
    private static MiniGameSlotResponse generateMiniGameSlot(int baseBetting) {
        MiniGameSlotResponse response = new MiniGameSlotResponse();
        int step = 0;
        long tongGiai = 0L;
        boolean chonTiep;
        Random rd = new Random();
        StringBuilder sb = new StringBuilder();
        do {
            if (rd.nextInt(100) + 1 > Constant.SLOT25_TANK_TI_LE_TRUOT[step]) {
                int indexCol = rd.nextInt(15);
                int prize = Constant.SLOT25_TANK_PRIZES[step][indexCol] * baseBetting;
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

    /**
     * Phương thức này được sử dụng để tính toán giải thưởng cho số lần xuất hiện của ITEM trên 1 LINE
     * mà không bao gồm việc tính toán số lần quay miễn phí và giải thưởng cho BONUS game
     *
     * @param line
     * @param awardList
     */
    public static void calculateMoneyAwardInLine(Line line, List<Slot25ExtendAward> awardList) {
        // số lương wild xuất hiện trên line
        int countWild = 0;
        // ánh xạ giữa item và số lượng xuất hiện của nó trên 1 Line
        Map<Byte, Integer> itemId2Count = new HashMap<>();
        // duyệt qua các cell trên 1 line để tính toán số lần xuất hiện
        for (int cellIndex = 0; cellIndex < line.getCells().size(); cellIndex++) {
            Cell cell = line.getCell(cellIndex);
            Slot25ExtendItem avengersItem = (Slot25ExtendItem) cell.getItem();
            Integer countNumberItem = itemId2Count.get(avengersItem.getId());
            if (countNumberItem == null) {
                countNumberItem = 1;
            } else {
                countNumberItem += 1;
            }
            itemId2Count.put(avengersItem.getId(), countNumberItem);

            if (avengersItem == Slot25ExtendItem.WILD) {
                countWild += 1;
            }
        }
        // WILD có thể thay thế tất cả items (trừ SCATTER, BONUS và chính nó)
        if (countWild > 0) {
            int finalCountWild = countWild;
            itemId2Count.forEach((id, numOfItem) -> {
                Slot25ExtendItem item = Slot25ExtendItem.findItem(id);
                if (item != Slot25ExtendItem.BONUS
                        && item != Slot25ExtendItem.SCATTER
                        && item != Slot25ExtendItem.WILD
                        && item != Slot25ExtendItem.JACKPOT) {
                    itemId2Count.put(id, numOfItem + finalCountWild);
                }
            });
        }
        // bắt đầu tính toán giải thưởng đạt được trên 1 line
        itemId2Count.forEach((id, countNumItem) -> {
            // Chỉ có item có số lần xuất hiện lớn hơn hoặc bằng 2 thì mới tính toán giải thưởng
            if (countNumItem >= 2) {
                Slot25ExtendItem item = Slot25ExtendItem.findItem(id);
                // Bởi vì BONUS và SCATTER không có giải thưởng tiền trên 1 LINE
                // nên ta có thể bỏ qua mà không cần tính toán
                if (item != Slot25ExtendItem.BONUS && item != Slot25ExtendItem.SCATTER) {
                    Slot25ExtendAward award = Slot25ExtendAwards.getAward(item, countNumItem);
                    if (award != null) {
                        awardList.add(award);
                    }
                }
            }
        });
    }

    /**
     * tính toán giải thưởng trên 1 line ở chế độ FREE SPIN
     *
     * @param line
     * @param awardList
     */
    public static void calculateFreeSpinAwardInLine(Line line, List<Slot25ExtendFreeSpinAward> awardList) {
        // ánh xạ giữa item và số lượng xuất hiện của nó trên 1 Line
        Map<Byte, Integer> itemId2Count = new HashMap<>();
        // duyệt qua các cell trên 1 line để tính toán số lần xuất hiện
        for (int cellIndex = 0; cellIndex < line.getCells().size(); cellIndex++) {
            Cell cell = line.getCell(cellIndex);
            Slot25ExtendFreeSpinItem item = (Slot25ExtendFreeSpinItem) cell.getItem();
            Integer countNumberItem = itemId2Count.get(item.getId());
            if (countNumberItem == null) {
                countNumberItem = 1;
            } else {
                countNumberItem += 1;
            }
            itemId2Count.put(item.getId(), countNumberItem);
        }
        // bắt đầu tính toán giải thưởng đạt được trên 1 line
        itemId2Count.forEach((id, countNumItem) -> {
            // Chỉ có item có số lần xuất hiện lớn hơn hoặc bằng 3 thì mới tính toán giải thưởng
            if (countNumItem >= 3) {
                Slot25ExtendFreeSpinItem item = Slot25ExtendFreeSpinItem.findItem(id);
                Slot25ExtendFreeSpinAward award = Slot25ExtendFreeSpinAwards.getAward(item, countNumItem);
                if (award != null) {
                    awardList.add(award);
                }
            }
        });
    }

    /**
     * Thay thế items trong 1 cột bằng WILD
     *
     * @param m
     * @return
     */
    public static Slot25ExtendItem[][] revertMatrix(Slot25ExtendItem[][] m) {
        Slot25ExtendItem[][] matrix = new Slot25ExtendItem[3][5];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (matrix[i][j] != null) continue;

                matrix[i][j] = m[i][j];
                if (matrix[i][j] != Slot25ExtendItem.WILD) continue;

                matrix[0][j] = Slot25ExtendItem.WILD;
                matrix[1][j] = Slot25ExtendItem.WILD;
                matrix[2][j] = Slot25ExtendItem.WILD;
            }
        }
        return matrix;
    }
}

