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
                    if (item == Slot20ExtendItem.WILD) {
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
                            && matrix[0][col] != Slot20ExtendItem.WILD
                            && matrix[1][col] != Slot20ExtendItem.WILD
                            && matrix[2][col] != Slot20ExtendItem.WILD) {
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
                || item == Slot20ExtendItem.WILD;
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
                    else
                        matrix[row][col] = Slot20ExtendItem.WILD;
                }
                if (!genRandom) continue;
                Slot20ExtendItem item = Slot20ExtendItem.JACKPOT;
                while (item == Slot20ExtendItem.JACKPOT || item == Slot20ExtendItem.WILD) {
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
        // số lương wild xuất hiện trên line
        int countWild = 0;
        int countJackpot = 0;
        // ánh xạ giữa item và số lượng xuất hiện của nó trên 1 Line
        Map<Byte, Integer> itemId2Count = new HashMap<>();
        // duyệt qua các cell trên 1 line để tính toán số lần xuất hiện
        for (int cellIndex = 0; cellIndex < line.getCells().size(); cellIndex++) {
            Cell cell = line.getCell(cellIndex);
            Slot20ExtendItem avengersItem = (Slot20ExtendItem) cell.getItem();
            Integer countNumberItem = itemId2Count.get(avengersItem.getId());
            if (countNumberItem == null) {
                countNumberItem = 1;
            } else {
                countNumberItem += 1;
            }
            itemId2Count.put(avengersItem.getId(), countNumberItem);

            if (avengersItem == Slot20ExtendItem.WILD) {
                countWild += 1;
            }

            if (avengersItem == Slot20ExtendItem.JACKPOT) {
                countJackpot += 1;
            }
        }
        // WILD có thể thay thế tất cả items (trừ SCATTER, BONUS và chính nó)
        if (countWild > 0) {
            int finalCountWild = countWild;
            itemId2Count.forEach((id, numOfItem) -> {
                Slot20ExtendItem item = Slot20ExtendItem.findItem(id);
                if (item != Slot20ExtendItem.BONUS
                        && item != Slot20ExtendItem.SCATTER
                        && item != Slot20ExtendItem.WILD) {
                    itemId2Count.put(id, numOfItem + finalCountWild);
                }
            });
        }

        // JACKPOT có thể thay thế tất cả items (trừ SCATTER, BONUS và chính nó)
        if (countJackpot > 0) {
            int finalCountJackpot = countJackpot;
            itemId2Count.forEach((id, numOfItem) -> {
                Slot20ExtendItem item = Slot20ExtendItem.findItem(id);
                if (item != Slot20ExtendItem.BONUS
                        && item != Slot20ExtendItem.SCATTER
                        && item != Slot20ExtendItem.JACKPOT) {
                    itemId2Count.put(id, numOfItem + finalCountJackpot);
                }
            });
        }


        // bắt đầu tính toán giải thưởng đạt được trên 1 line
        itemId2Count.forEach((id, countNumItem) -> {
            // Chỉ có item có số lần xuất hiện lớn hơn hoặc bằng 2 thì mới tính toán giải thưởng
            if (countNumItem >= 2) {
                Slot20ExtendItem item = Slot20ExtendItem.findItem(id);
                // Bởi vì BONUS không có giải thưởng tiền trên 1 LINE
                // nên ta có thể bỏ qua mà không cần tính toán
                if (item != Slot20ExtendItem.BONUS) {
                    Slot20ExtendAward award = Slot20ExtendAwards.getAward(item, countNumItem);
                    if (award != null) {
                        awardList.add(award);
                    }
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
    public static Slot20ExtendItem[][] revertMatrix(Slot20ExtendItem[][] m) {
        Slot20ExtendItem[][] matrix = new Slot20ExtendItem[3][5];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (matrix[i][j] != null) continue;

                matrix[i][j] = m[i][j];
                if (matrix[i][j] != Slot20ExtendItem.WILD) continue;

                matrix[0][j] = Slot20ExtendItem.WILD;
                matrix[1][j] = Slot20ExtendItem.WILD;
                matrix[2][j] = Slot20ExtendItem.WILD;
            }
        }
        return matrix;
    }
}

