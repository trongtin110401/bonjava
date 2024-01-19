/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.utils;

import game.modules.slot.entities.slot.Cell;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.MiniGameSlotResponse;
import game.modules.slot.entities.slot.line25basic.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Slot25BasicUtil {

    public static SlotBasic25Item[][] generateMatrix() {
        Slot25BasicItems items = new Slot25BasicItems();
        SlotBasic25Item[][] matrix = new SlotBasic25Item[3][5];
        Random rd = new Random();
        int n = rd.nextInt(3);
        for (int row = 0; row < 3; ++row) {
            int r = n + row;
            if (r > 2) {
                r -= 3;
            }
            for (int column = 0; column < 5; ++column) {
                boolean isContinueGenerate = true;
                SlotBasic25Item item = null;
                while (isContinueGenerate) {
                    isContinueGenerate = false;
                    item = items.random(column);
                    if (!Slot25BasicUtil.isSpecialItem(item)) {
                        continue;
                    }
                    if (item == SlotBasic25Item.WILD) {
                        if (!Slot25BasicUtil.isSpecialItem(matrix[0][column])
                                && !Slot25BasicUtil.isSpecialItem(matrix[1][column])
                                && !Slot25BasicUtil.isSpecialItem(matrix[2][column])) {
                            continue;
                        }
                        isContinueGenerate = true;
                        items.refundItem(item, column);
                        continue;
                    }
                    if (matrix[0][column] != item
                            && matrix[1][column] != item
                            && matrix[2][column] != item
                            && matrix[0][column] != SlotBasic25Item.WILD
                            && matrix[1][column] != SlotBasic25Item.WILD
                            && matrix[2][column] != SlotBasic25Item.WILD) {
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

    public static boolean isSpecialItem(SlotBasic25Item item) {
        return item == SlotBasic25Item.BONUS || item == SlotBasic25Item.SCATTER || item == SlotBasic25Item.JACKPOT || item == SlotBasic25Item.WILD;
    }

    public static SlotBasic25Item[][] generateMatrixFreeSpin(String itemsWild) {
        int i;
        String[] arr = itemsWild.split(",");
        Slot25BasicFreeSpinItems items = new Slot25BasicFreeSpinItems();
        SlotBasic25Item[][] matrix = new SlotBasic25Item[3][5];
        if (arr.length > 0) {
            for (i = 0; i < arr.length - 1; i += 2) {
                int r = Integer.parseInt(arr[i]);
                int c = Integer.parseInt(arr[i + 1]);
                matrix[r][c] = SlotBasic25Item.WILD;
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

    public static SlotBasic25Item[][] generateMatrixNoHu(String[] lineArr) {
        SlotBasic25Item[][] matrix = new SlotBasic25Item[3][5];
        Random rd = new Random();
        int n = rd.nextInt(lineArr.length);
        int indexLineNoHu = Integer.parseInt(lineArr[n]) - 1;
        Slot25BasicLines lines = new Slot25BasicLines();
        Slot25BasicItems items = new Slot25BasicItems();
        Line<SlotBasic25Item> lineNoHu = lines.get(indexLineNoHu);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                boolean genRandom = true;
                for (int k = 0; k < lineNoHu.getCells().size(); ++k) {
                    if (i != lineNoHu.getCell(k).getRow() || j != lineNoHu.getCell(k).getCol()) continue;
                    genRandom = false;
                    matrix[i][j] = SlotBasic25Item.JACKPOT;
                }
                if (!genRandom) continue;
                SlotBasic25Item item = SlotBasic25Item.JACKPOT;
                while (item == SlotBasic25Item.JACKPOT || item == SlotBasic25Item.WILD) {
                    item = items.random(j);
                }
                matrix[i][j] = item;
            }
        }
        return matrix;
    }

    public static String matrixToString(SlotBasic25Item[][] matrix) {
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

    public static Line getLine(Slot25BasicLines lines, SlotBasic25Item[][] matrix, int lineIndex) {
        Line<SlotBasic25Item> line = lines.get(lineIndex - 1);
        for (Cell<SlotBasic25Item> cell : line.getCells()) {
            SlotBasic25Item itemInMatrix = matrix[cell.getRow()][cell.getCol()];
            cell.setItem(itemInMatrix);
        }
        return line;
    }

    public static MiniGameSlotResponse buildBonusGameData(int betValue, int countBonus) {
        Random rd = new Random();
        int indexRatioCol = rd.nextInt(3);
        int indexRatioRow = countBonus - 3;
        int ratio = Constant.AVENGERS_BONUS_RATIO[indexRatioRow][indexRatioCol];
        MiniGameSlotResponse res = Slot25BasicUtil.generateMiniGameSlot(betValue);
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

    public static void calculateAward(Line line, List<Slot25BasicAward> awardList) {
        int countNumItems = 0;
        SlotBasic25Item firstLineItem = (SlotBasic25Item) line.getCell(0).getItem();
        if (firstLineItem != SlotBasic25Item.BONUS && firstLineItem != SlotBasic25Item.SCATTER) {
            for (int i = 0; i < line.getCells().size(); ++i) {
                byte itemId = ((SlotBasic25Item) line.getCell(i).getItem()).getId();
                if (itemId == firstLineItem.getId()
                        || firstLineItem.getId() != SlotBasic25Item.JACKPOT.getId()
                        && itemId == SlotBasic25Item.WILD.getId()) {
                    ++countNumItems;
                }
            }

            Slot25BasicAward award;
            if (countNumItems >= 3 && (award = Slot25BasicAwards.getAward(firstLineItem, countNumItems)) != null) {
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
    public static void calculateMoneyAwardInLine(Line line, List<Slot25BasicAward> awardList) {
        // số lương wild xuất hiện trên line
        int countWild = 0;
        // ánh xạ giữa item và số lượng xuất hiện của nó trên 1 Line
        Map<Byte, Integer> itemId2Count = new HashMap<>();
        // duyệt qua các cell trên 1 line để tính toán số lần xuất hiện
        for (int cellIndex = 0; cellIndex < line.getCells().size(); cellIndex++) {
            Cell cell = line.getCell(cellIndex);
            SlotBasic25Item avengersItem = (SlotBasic25Item) cell.getItem();
            Integer countNumberItem = itemId2Count.get(avengersItem.getId());
            if (countNumberItem == null) {
                countNumberItem = 1;
            } else {
                countNumberItem += 1;
            }
            itemId2Count.put(avengersItem.getId(), countNumberItem);

            if (avengersItem == SlotBasic25Item.WILD) {
                countWild += 1;
            }
        }
        // WILD có thể thay thế tất cả items (trừ SCATTER, BONUS và chính nó)
        if (countWild > 0) {
            int finalCountWild = countWild;
            itemId2Count.forEach((id, numOfItem) -> {
                SlotBasic25Item item = SlotBasic25Item.findItem(id);
                if (item != SlotBasic25Item.BONUS
                        && item != SlotBasic25Item.SCATTER
                        && item != SlotBasic25Item.WILD) {
                    itemId2Count.put(id, numOfItem + finalCountWild);
                }
            });
        }
        // bắt đầu tính toán giải thưởng đạt được trên 1 line
        itemId2Count.forEach((id, countNumItem) -> {
            // Chỉ có item có số lần xuất hiện lớn hơn hoặc bằng 2 thì mới tính toán giải thưởng
            if (countNumItem >= 2) {
                SlotBasic25Item item = SlotBasic25Item.findItem(id);
                // Bởi vì BONUS và SCATTER không có giải thưởng tiền trên 1 LINE
                // nên ta có thể bỏ qua mà không cần tính toán
                if (item != SlotBasic25Item.BONUS && item != SlotBasic25Item.SCATTER) {
                    Slot25BasicAward award = Slot25BasicAwards.getAward(item, countNumItem);
                    if (award != null) {
                        awardList.add(award);
                    }
                }
            }
        });
    }

    public static void calculateFreeSpinLine(Line line, List<Slot25BasicFreeSpinAward> awardList) {
        int countNumItems = 0;
        SlotBasic25Item itemSample = (SlotBasic25Item) line.getCell(0).getItem();
        if (itemSample != SlotBasic25Item.BONUS && itemSample != SlotBasic25Item.SCATTER && itemSample != SlotBasic25Item.JACKPOT) {
            Slot25BasicFreeSpinAward award;
            for (int j = 0; j < line.getCells().size() && (line.getCell(j).getItem() == itemSample || line.getCell(j).getItem() == SlotBasic25Item.WILD); ++j) {
                ++countNumItems;
            }
            if (countNumItems >= 3 && (award = Slot25BasicFreeSpinAwardManager.getAward(itemSample, countNumItems)) != null) {
                awardList.add(award);
            }
        }
    }

    public static SlotBasic25Item[][] revertMatrix(SlotBasic25Item[][] m) {
        SlotBasic25Item[][] matrix = new SlotBasic25Item[3][5];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                if (matrix[i][j] != null) continue;

                matrix[i][j] = m[i][j];
                if (matrix[i][j] != SlotBasic25Item.WILD) continue;

                matrix[0][j] = SlotBasic25Item.WILD;
                matrix[1][j] = SlotBasic25Item.WILD;
                matrix[2][j] = SlotBasic25Item.WILD;
            }
        }
        return matrix;
    }
}

