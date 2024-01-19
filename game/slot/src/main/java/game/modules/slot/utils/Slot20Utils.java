
package game.modules.slot.utils;

import game.modules.slot.entities.slot.Cell;
import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.line20basic.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Slot20Utils {

    public static Slot20Item[][] generateMatrix() {
        Slot20Items items = new Slot20Items();
        Slot20Item[][] matrix = new Slot20Item[3][5];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                matrix[i][j] = items.random();
            }
        }
        return matrix;
    }

    public static Slot20Item[][] generateJackpotMatrix(String[] lineArr) {
        Slot20Item[][] matrix = new Slot20Item[3][5];
        Random rd = new Random();
        int n = rd.nextInt(lineArr.length);
        int indexLineNoHu = Integer.parseInt(lineArr[n]) - 1;
        Slot20Lines lines = new Slot20Lines();
        Slot20Items items = new Slot20Items();
        Slot20Line lineNoHu = lines.get(indexLineNoHu);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                boolean genRandom = true;
                for (int k = 0; k < lineNoHu.getCells().size(); ++k) {
                    if (i != lineNoHu.getCell(k).getRow() || j != lineNoHu.getCell(k).getCol()) continue;
                    genRandom = false;
                    matrix[i][j] = Slot20Item.JACKPOT;
                }
                if (!genRandom) continue;
                matrix[i][j] = items.random();
            }
        }
        return matrix;
    }

    public static String matrixToString(Slot20Item[][] matrix) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                builder.append(",");
                builder.append(matrix[i][j].getId());
            }
        }
        if (builder.length() > 0) {
            builder.deleteCharAt(0);
        }
        return builder.toString();
    }

    public static Slot20Line getLine(Slot20Lines lines, Slot20Item[][] matrix, int lineIndex) {
        Slot20Line line = lines.get(lineIndex - 1);
        for (Slot20Cell cell : line.getCells()) {
            Slot20Item itemInMatrix = matrix[cell.getRow()][cell.getCol()];
            cell.setItem(itemInMatrix);
        }
        return line;
    }

    public static void calculateLine(Slot20Line line, List<Slot20Award> awardList) {
        for (int i = 0; i < line.getCells().size(); ++i) {
            Slot20Award award;
            int countNumItems = 0;
            Slot20Item itemSample = line.getItem(i);
            for (int j = 0; j < line.getCells().size(); ++j) {
                if (line.getItem(j) != itemSample && line.getItem(j) != Slot20Item.FREE_SPIN) continue;
                ++countNumItems;
            }
            if (countNumItems < 3
                    || (award = Slot20Awards.getAward(itemSample, countNumItems)) == null
                    || Slot20Utils.checkAwardExist(awardList, award))
                continue;
            awardList.add(award);
        }
    }

    /**
     * Ph??ng th?c này ???c s? d?ng ?? tính toán gi?i th??ng cho s? l?n xu?t hi?n c?a ITEM trên 1 LINE
     * mà không bao g?m vi?c tính toán s? l?n quay mi?n phí và gi?i th??ng cho BONUS game
     *
     * @param line
     * @param awardList
     */
    public static void calculateAwardInLine(Slot20Line line, List<Slot20Award> awardList) {
        // ánh x? gi?a item và s? l??ng xu?t hi?n c?a nó trên 1 Line
        Map<Byte, Integer> itemId2Count = new HashMap<>();
        // duy?t qua các cell trên 1 line ?? tính toán s? l?n xu?t hi?n
        for (int cellIndex = 0; cellIndex < line.getCells().size(); cellIndex++) {
            Slot20Cell cell = line.getCell(cellIndex);
            Slot20Item item = (Slot20Item) cell.getItem();
            Integer countNumberItem = itemId2Count.get(item.getId());
            if (countNumberItem == null) {
                countNumberItem = 1;
            } else {
                countNumberItem += 1;
            }
            itemId2Count.put(item.getId(), countNumberItem);
        }
        // sau khi ánh x?
        // tính toán gi?i th??ng ??t ???c trên 1 line
        itemId2Count.forEach((id, countNumItem) -> {
            // Ch? có item có s? l?n xu?t hi?n l?n h?n ho?c b?ng 2 thì m?i tính toán gi?i th??ng
            if (countNumItem >= 2) {
                Slot20Item item = Slot20Item.findItem(id);
                Slot20Award award = Slot20Awards.getAward(item, countNumItem);
                if (award != null) {
                    awardList.add(award);
                }
            }
        });
    }

    private static boolean checkAwardExist(List<Slot20Award> awardList, Slot20Award awardLine) {
        for (Slot20Award award : awardList) {
            if (award != awardLine) continue;
            return true;
        }
        return false;
    }
}

