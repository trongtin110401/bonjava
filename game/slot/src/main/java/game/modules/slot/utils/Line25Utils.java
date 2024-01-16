/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.utils;

import game.modules.slot.entities.slot.Cell;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.avengers.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Line25Utils {

    public static Line25Item[][] generateMatrix() {
        Line25Items items = new Line25Items();
        Line25Item[][] matrix = new Line25Item[3][5];
        Random rd = new Random();
        int n = rd.nextInt(3);
        for (int row = 0; row < 3; ++row) {
            int r = n + row;
            if (r > 2) {
                r -= 3;
            }
            for (int column = 0; column < 5; ++column) {
                boolean isContinueGenerate = true;
                Line25Item item = null;
                while (isContinueGenerate) {
                    isContinueGenerate = false;
                    item = items.random(column);
                    if (!Line25Utils.isSpecialItem(item)) {
                        continue;
                    }
                    if (item == Line25Item.WILD) {
                        if (!Line25Utils.isSpecialItem(matrix[0][column])
                                && !Line25Utils.isSpecialItem(matrix[1][column])
                                && !Line25Utils.isSpecialItem(matrix[2][column])) {
                            continue;
                        }
                        isContinueGenerate = true;
                        items.refundItem(item, column);
                        continue;
                    }
                    if (matrix[0][column] != item
                            && matrix[1][column] != item
                            && matrix[2][column] != item
                            && matrix[0][column] != Line25Item.WILD
                            && matrix[1][column] != Line25Item.WILD
                            && matrix[2][column] != Line25Item.WILD) {
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

    public static boolean isSpecialItem(Line25Item item) {
        return item == Line25Item.BONUS || item == Line25Item.SCATTER || item == Line25Item.JACKPOT || item == Line25Item.WILD;
    }

    public static Line25Item[][] generateMatrixFreeSpin(String itemsWild) {
        int i;
        String[] arr = itemsWild.split(",");
        Line25FreeSpinItems items = new Line25FreeSpinItems();
        Line25Item[][] matrix = new Line25Item[3][5];
        if (arr.length > 0) {
            for (i = 0; i < arr.length - 1; i += 2) {
                int r = Integer.parseInt(arr[i]);
                int c = Integer.parseInt(arr[i + 1]);
                matrix[r][c] = Line25Item.WILD;
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

    public static Line25Item[][] generateMatrixNoHu(String[] lineArr) {
        Line25Item[][] matrix = new Line25Item[3][5];
        Random rd = new Random();
        int n = rd.nextInt(lineArr.length);
        int indexLineNoHu = Integer.parseInt(lineArr[n]) - 1;
        Line25Lines lines = new Line25Lines();
        Line25Items items = new Line25Items();
        Line<Line25Item> lineNoHu = lines.get(indexLineNoHu);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                boolean genRandom = true;
                for (int k = 0; k < lineNoHu.getCells().size(); ++k) {
                    if (i != lineNoHu.getCell(k).getRow() || j != lineNoHu.getCell(k).getCol()) continue;
                    genRandom = false;
                    matrix[i][j] = Line25Item.JACKPOT;
                }
                if (!genRandom) continue;
                Line25Item item = Line25Item.JACKPOT;
                while (item == Line25Item.JACKPOT || item == Line25Item.WILD) {
                    item = items.random(j);
                }
                matrix[i][j] = item;
            }
        }
        return matrix;
    }

    public static String matrixToString(Line25Item[][] matrix) {
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

    public static Line getLine(Line25Lines lines, Line25Item[][] matrix, int lineIndex) {
        Line<Line25Item> line = lines.get(lineIndex - 1);
        for (Cell<Line25Item> cell : line.getCells()) {
            Line25Item itemInMatrix = matrix[cell.getRow()][cell.getCol()];
            cell.setItem(itemInMatrix);
        }
        return line;
    }

    public static MiniGameSlotResponse buildBonusGameData(int betValue, int countBonus) {
        Random rd = new Random();
        int indexRatioCol = rd.nextInt(3);
        int indexRatioRow = countBonus - 3;
        int ratio = Constant.AVENGERS_BONUS_RATIO[indexRatioRow][indexRatioCol];
        MiniGameSlotResponse res = Line25Utils.generateMiniGameSlot(betValue);
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


//        System.out.println(matrixToString(generateMatrix()));

        Map<String, Integer> counter = new HashMap<>();
        AtomicInteger countWild = new AtomicInteger();
        String items = "A,A,WILD,WILD,BONUS";
        Arrays.stream(items.split(","))
                .forEach(item -> {
                    Integer count = counter.get(item);
                    if (count == null) {
                        count = 1;
                    } else {
                        count += 1;
                    }
                    counter.put(item, count);

                    // count wild
                    if (item.equals("WILD")) {
                        countWild.addAndGet(1);
                    }
                });
        // if wild appears, increase item
        counter.forEach((item, count) -> {
            if (!item.equals("BONUS") && !item.equals("SCATTER") && !item.equals("WILD")) {
                int newCount = counter.get(item) + countWild.get();
                counter.put(item, newCount);
            }
        });

        System.out.println(counter.toString());
    }

    public static void calculateAward(Line line, List<Line25Award> awardList) {
        int countNumItems = 0;
        Line25Item firstLineItem = (Line25Item) line.getCell(0).getItem();
        if (firstLineItem != Line25Item.BONUS && firstLineItem != Line25Item.SCATTER) {
            for (int i = 0; i < line.getCells().size(); ++i) {
                byte itemId = ((Line25Item) line.getCell(i).getItem()).getId();
                if (itemId == firstLineItem.getId()
                        || firstLineItem.getId() != Line25Item.JACKPOT.getId()
                        && itemId == Line25Item.WILD.getId()) {
                    ++countNumItems;
                }
            }

            Line25Award award;
            if (countNumItems >= 3 && (award = Line25AwardManager.getAward(firstLineItem, countNumItems)) != null) {
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
    public static void calculateMoneyAwardInLine(Line line, List<Line25Award> awardList) {
        // số lương wild xuất hiện trên line
        int countWild = 0;
        // ánh xạ giữa item và số lượng xuất hiện của nó trên 1 Line
        Map<Byte, Integer> itemId2Count = new HashMap<>();
        // duyệt qua các cell trên 1 line để tính toán số lần xuất hiện
        for (int cellIndex = 0; cellIndex < line.getCells().size(); cellIndex++) {
            Cell cell = line.getCell(cellIndex);
            Line25Item avengersItem = (Line25Item) cell.getItem();
            Integer countNumberItem = itemId2Count.get(avengersItem.getId());
            if (countNumberItem == null) {
                countNumberItem = 1;
            } else {
                countNumberItem += 1;
            }
            itemId2Count.put(avengersItem.getId(), countNumberItem);

            if (avengersItem == Line25Item.WILD) {
                countWild += 1;
            }
        }
        // WILD có thể thay thế tất cả items (trừ SCATTER, BONUS và chính nó)
        if (countWild > 0) {
            int finalCountWild = countWild;
            itemId2Count.forEach((id, numOfItem) -> {
                Line25Item item = Line25Item.findItem(id);
                if (item != Line25Item.BONUS
                        && item != Line25Item.SCATTER
                        && item != Line25Item.WILD) {
                    itemId2Count.put(id, numOfItem + finalCountWild);
                }
            });
        }
        // bắt đầu tính toán giải thưởng đạt được trên 1 line
        itemId2Count.forEach((id, countNumItem) -> {
            // Chỉ có item có số lần xuất hiện lớn hơn hoặc bằng 2 thì mới tính toán giải thưởng
            if (countNumItem >= 2) {
                Line25Item item = Line25Item.findItem(id);
                // Bởi vì BONUS và SCATTER không có giải thưởng tiền trên 1 LINE
                // nên ta có thể bỏ qua mà không cần tính toán
                if (item != Line25Item.BONUS && item != Line25Item.SCATTER) {
                    Line25Award award = Line25AwardManager.getAward(item, countNumItem);
                    if (award != null) {
                        awardList.add(award);
                    }
                }
            }
        });
    }

    public static void calculateFreeSpinLine(Line line, List<Line25FreeSpinAward> awardList) {
        int countNumItems = 0;
        Line25Item itemSample = (Line25Item) line.getCell(0).getItem();
        if (itemSample != Line25Item.BONUS && itemSample != Line25Item.SCATTER && itemSample != Line25Item.JACKPOT) {
            Line25FreeSpinAward award;
            for (int j = 0; j < line.getCells().size() && (line.getCell(j).getItem() == itemSample || line.getCell(j).getItem() == Line25Item.WILD); ++j) {
                ++countNumItems;
            }
            if (countNumItems >= 3 && (award = Line25FreeSpinAwardManager.getAward(itemSample, countNumItems)) != null) {
                awardList.add(award);
            }
        }
    }

    public static Line25Item[][] revertMatrix(Line25Item[][] m) {
        Line25Item[][] matrix = new Line25Item[3][5];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (matrix[i][j] != null) continue;

                matrix[i][j] = m[i][j];
                if (matrix[i][j] != Line25Item.WILD) continue;

                matrix[0][j] = Line25Item.WILD;
                matrix[1][j] = Line25Item.WILD;
                matrix[2][j] = Line25Item.WILD;
            }
        }
        return matrix;
    }
}

