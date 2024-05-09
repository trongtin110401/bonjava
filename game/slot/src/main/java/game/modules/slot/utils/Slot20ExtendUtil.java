/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.utils;

import game.modules.slot.entities.slot.Cell;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.line20extend.*;

import java.util.*;

public class Slot20ExtendUtil {

    public static Slot20ExtendItem[][] generateMatrix() {
        Slot20ExtendItems items = new Slot20ExtendItems();
        Slot20ExtendItem[][] matrix = new Slot20ExtendItem[3][5];
        Random rd = new Random();
        int n = rd.nextInt(3);
        for (int row = 0; row < 3; ++row) {
            int r = n + row;
            if (r > 2) {
                r -= 3;
            }
            for (int col = 0; col < 5; ++col) {
                boolean isContinueGenerate = true;
                Slot20ExtendItem item = null;
                while (isContinueGenerate) {
                    isContinueGenerate = false;
                    item = items.random(col);
                    if (!Slot20ExtendUtil.isSpecialItem(item)) {
                        continue;
                    }
                    if (item == Slot20ExtendItem.WILD || item == Slot20ExtendItem.WILD2) {
                        if (!Slot20ExtendUtil.isSpecialItem(matrix[0][col])
                                && !Slot20ExtendUtil.isSpecialItem(matrix[1][col])
                                && !Slot20ExtendUtil.isSpecialItem(matrix[2][col])) {
                            continue;
                        }
                        isContinueGenerate = true;
                        items.refundItem(item, col);
                        continue;
                    }
                    if (matrix[0][col] != item
                            && matrix[1][col] != item
                            && matrix[2][col] != item
                            && (matrix[0][col] != Slot20ExtendItem.WILD || matrix[0][col] != Slot20ExtendItem.WILD2)
                            && (matrix[1][col] != Slot20ExtendItem.WILD || matrix[0][col] != Slot20ExtendItem.WILD2)
                            && (matrix[2][col] != Slot20ExtendItem.WILD || matrix[0][col] != Slot20ExtendItem.WILD2)) {
                        continue;
                    }
                    isContinueGenerate = true;
                    items.refundItem(item, col);
                }
                matrix[r][col] = item;
            }
        }
        return matrix;
    }

    public static boolean isSpecialItem(Slot20ExtendItem item) {
        return item == Slot20ExtendItem.BONUS
                || item == Slot20ExtendItem.SCATTER
                || item == Slot20ExtendItem.JACKPOT
                || item == Slot20ExtendItem.WILD
                || item == Slot20ExtendItem.WILD2;
    }

    public static Slot20ExtendItem[][] generateMatrixNoHu(String[] lineArr) {
        Slot20ExtendItem[][] matrix = new Slot20ExtendItem[3][5];
        Random rd = new Random();
        int n = rd.nextInt(lineArr.length);
        int indexLineNoHu = Integer.parseInt(lineArr[n]) - 1;
        Slot20ExtendLines lines = new Slot20ExtendLines();
        Slot20ExtendItems items = new Slot20ExtendItems();
        Line<Slot20ExtendItem> lineNoHu = lines.get(indexLineNoHu);
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 5; ++col) {
                boolean genRandom = true;
                for (int celIndex = 0; celIndex < lineNoHu.getCells().size(); ++celIndex) {
                    if (row != lineNoHu.getCell(celIndex).getRow() || col != lineNoHu.getCell(celIndex).getCol())
                        continue;
                    genRandom = false;
                    if (col == 0 || col == 2 || col == 4)
                        matrix[row][col] = Slot20ExtendItem.JACKPOT;
                    else if (col == 1)
                        matrix[row][col] = Slot20ExtendItem.WILD;
                    else
                        matrix[row][col] = Slot20ExtendItem.WILD2;
                }
                if (!genRandom) continue;
                Slot20ExtendItem item = Slot20ExtendItem.JACKPOT;
                while (item == Slot20ExtendItem.JACKPOT || item == Slot20ExtendItem.WILD || item == Slot20ExtendItem.WILD2) {
                    item = items.random(col);
                }
                matrix[row][col] = item;
            }
        }
        return matrix;
    }

    public static String matrixToString(Slot20ExtendItem[][] matrix) {
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


    public static Line getLine(Slot20ExtendLines lines, Slot20ExtendItem[][] matrix, int lineIndex) {
        Line<Slot20ExtendItem> line = lines.get(lineIndex - 1);
        for (Cell<Slot20ExtendItem> cell : line.getCells()) {
            Slot20ExtendItem itemInMatrix = matrix[cell.getRow()][cell.getCol()];
            cell.setItem(itemInMatrix);
        }
        return line;
    }

    public static int randomInt(int min, int max) {
        Random random = new Random();
        return random.ints(min, max)
                .findFirst()
                .getAsInt();
    }

    public static MiniGameSlotResponse buildBonusGameData(int betValue, int countBonus) {
        int[] prices = new int[3];
        for (int i = 0; i < 3; i++) {
            int ratio = 0;
            if (countBonus == 3) {
                ratio = randomInt(20, 100);
            } else if (countBonus == 4) {
                ratio = randomInt(20, 200);
            } else {
                ratio = randomInt(20, 500);
            }
            prices[i] = betValue * ratio;
        }
        MiniGameSlotResponse miniGameSlotResponse = new MiniGameSlotResponse();
        miniGameSlotResponse.setTotalPrize(Arrays.stream(prices).sum());
        miniGameSlotResponse.setPrizes(Arrays.stream(prices)
                .mapToObj(String::valueOf)
                .reduce((s1, s2) -> s1 + "," + s2)
                .orElse(""));
        miniGameSlotResponse.setRatio(0);
        return miniGameSlotResponse;
    }

    /**
     * Phương thức này được sử dụng để tính toán giải thưởng cho số lần xuất hiện của ITEM trên 1 LINE
     * mà không bao gồm việc tính toán số lần quay miễn phí và giải thưởng cho BONUS game
     *
     * @param line
     * @param awardList
     */
    public static void calculateMoneyAwardInLine(Line line, List<Slot20ExtendAward> awardList) {
        // kiểm tra jackpot trước
        List cels = line.getCells();
        if (cels.get(0) == Slot20ExtendItem.JACKPOT
                && (cels.get(1) == Slot20ExtendItem.WILD)
                && cels.get(2) == Slot20ExtendItem.JACKPOT
                && cels.get(3) == Slot20ExtendItem.WILD2
                && cels.get(4) == Slot20ExtendItem.JACKPOT) {
            awardList.add(Slot20ExtendAward.JACKPOT);
            return;
        }

        // Duyệt mảng từ trái sang phải
        Map<Byte, Integer> item2Count = new HashMap<>();
        for (int i = 0; i < line.getCells().size(); i++) {
            int count = 1; // Biến đếm số lượng trùng lặp
            Slot20ExtendItem currentItem = (Slot20ExtendItem) line.getCell(i).getItem(); // item hiện tại
            // Bỏ qua không đếm do các items này không có phần thưởng hệ số
            if (currentItem == Slot20ExtendItem.WILD
                    || currentItem == Slot20ExtendItem.WILD2
                    || currentItem == Slot20ExtendItem.BONUS
                    || currentItem == Slot20ExtendItem.JACKPOT) {
                continue;
            }
            // So sánh item hiện tại với item tiếp theo
            for (int j = i + 1; j < line.getCells().size(); j++) {
                Slot20ExtendItem nextItem = (Slot20ExtendItem) line.getCell(j).getItem();
                if (currentItem == nextItem) {
                    count += 1;
                } else if (nextItem == Slot20ExtendItem.WILD || nextItem == Slot20ExtendItem.WILD2 || nextItem == Slot20ExtendItem.JACKPOT) {
                    count += 1;
                } else {
                    break;
                }
            }
            if (count > 1) {
                int finalCount = count;
                item2Count.compute(currentItem.getId(), (key, oldValue) -> {
                    if (oldValue == null) {
                        return finalCount;
                    } else {
                        return Math.max(finalCount, oldValue);
                    }
                });
            }
        }

        // bắt đầu tính toán giải thưởng đạt được trên 1 line
        item2Count.forEach((id, countNumItem) -> {
            // Bởi vì BONUS không có giải thưởng tiền trên 1 LINE
            // nên ta có thể bỏ qua mà không cần tính toán
            Slot20ExtendItem item = Slot20ExtendItem.findItem(id);
            Slot20ExtendAward award = Slot20ExtendAwards.getAward(item, countNumItem);
            if (award != null) {
                awardList.add(award);
            }
        });
    }

//    /**
//     * Thay thế items trong 1 cột bằng WILD
//     *
//     * @param m
//     * @return
//     */
//    public static Slot20ExtendItem[][] revertMatrix(Slot20ExtendItem[][] m) {
//        Slot20ExtendItem[][] matrix = new Slot20ExtendItem[3][5];
//        for (int row = 0; row < 3; ++row) {
//            for (int col = 0; col < 5; ++col) {
//                if (matrix[row][col] != null) continue;
//
//                matrix[row][col] = m[row][col];
//                if (matrix[row][col] != Slot20ExtendItem.WILD && matrix[row][col] != Slot20ExtendItem.WILD2) continue;
//
//                if (matrix[row][col] == Slot20ExtendItem.WILD) {
//                    matrix[0][col] = Slot20ExtendItem.WILD;
//                    matrix[1][col] = Slot20ExtendItem.WILD;
//                    matrix[2][col] = Slot20ExtendItem.WILD;
//                } else if (matrix[row][col] == Slot20ExtendItem.WILD2) {
//                    matrix[0][col] = Slot20ExtendItem.WILD2;
//                    matrix[1][col] = Slot20ExtendItem.WILD2;
//                    matrix[2][col] = Slot20ExtendItem.WILD2;
//                }
//            }
//        }
//        return matrix;
//    }

    /**
     * Thay thế items trong 1 cột bằng WILD
     *
     * @param matrix
     * @return
     */
    public static Slot20ExtendItem[][] revertMatrix(Slot20ExtendItem[][] matrix) {
        Slot20ExtendItem[][] newMatrix = new Slot20ExtendItem[3][5];
        for (int col = 0; col < 5; col++) {
            for (int row = 0; row < 3; row++) {
                if (newMatrix[row][col] != null) continue;
                newMatrix[row][col] = matrix[row][col];
                if (col == 1 || col == 3) {
                    if (matrix[row][col] == Slot20ExtendItem.WILD) {
                        matrix[0][col] = Slot20ExtendItem.WILD;
                        matrix[1][col] = Slot20ExtendItem.WILD;
                        matrix[2][col] = Slot20ExtendItem.WILD;
                    } else if (matrix[row][col] == Slot20ExtendItem.WILD2) {
                        matrix[0][col] = Slot20ExtendItem.WILD2;
                        matrix[1][col] = Slot20ExtendItem.WILD2;
                        matrix[2][col] = Slot20ExtendItem.WILD2;
                    }
                }
            }
        }
        return newMatrix;
    }
}

