package game.modules.slot.utils;


import game.modules.slot.entities.slot.line20basic.*;

import java.util.List;
import java.util.Random;

public class Live20Utils {

    public static Line20Item[][] generateMatrix() {
        Line20Items items = new Line20Items();
        Line20Item[][] matrix = new Line20Item[3][5];
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                matrix[i][j] = items.random();
            }
        }
        return matrix;
    }

    public static Line20Item[][] generateMatrixNoHu(String[] lineArr) {
        Line20Item[][] matrix = new Line20Item[3][5];
        Random rd = new Random();
        int n = rd.nextInt(lineArr.length);
        int indexLineNoHu = Integer.parseInt(lineArr[n]) - 1;
        Line20Lines lines = new Line20Lines();
        Line20Items items = new Line20Items();
        Line20 lineNoHu = lines.get(indexLineNoHu);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 5; ++j) {
                boolean genRandom = true;
                for (int k = 0; k < lineNoHu.getCells().size(); ++k) {
                    if (i != lineNoHu.getCell(k).getRow() || j != lineNoHu.getCell(k).getCol()) continue;
                    genRandom = false;
                    matrix[i][j] = Line20Item.JACKPOT;
                }
                if (!genRandom) continue;
                matrix[i][j] = items.random();
            }
        }
        return matrix;
    }

    public static String matrixToString(Line20Item[][] matrix) {
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

    public static Line20 getLine(Line20Lines lines, Line20Item[][] matrix, int lineIndex) {
        Line20 line = lines.get(lineIndex - 1);
        for (Line20Cell cell : line.getCells()) {
            Line20Item itemInMatrix = matrix[cell.getRow()][cell.getCol()];
            cell.setItem(itemInMatrix);
        }
        return line;
    }

    public static void calculateLine(Line20 line, List<Line20Award> awardList) {
        for (int i = 0; i < line.getCells().size(); ++i) {
            Line20Award award;
            int countNumItems = 0;
            Line20Item itemSample = line.getItem(i);
            for (int j = 0; j < line.getCells().size(); ++j) {
                if (line.getItem(j) != itemSample && line.getItem(j) != Line20Item.FREE_SPIN) continue;
                ++countNumItems;
            }
            if (countNumItems < 3
                    || (award = Line20Awards.getAward(itemSample, countNumItems)) == null
                    || Live20Utils.checkAwardExist(awardList, award))
                continue;
            awardList.add(award);
        }
    }

    private static boolean checkAwardExist(List<Line20Award> awardList, Line20Award awardLine) {
        for (Line20Award award : awardList) {
            if (award != awardLine) continue;
            return true;
        }
        return false;
    }
}

