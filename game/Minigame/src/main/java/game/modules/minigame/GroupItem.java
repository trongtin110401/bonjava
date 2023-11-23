/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.modules.minigame;


public class GroupItem {
    public static final int DIAMOND_TOTAL_ITEM = 9;
    public static final int DIAMOND_NUMBER_ITEM_ONE_LINE = 3;

    public static final long DIAMOND_PRIZE_AMOUNT_THREE_VIOLET = 85;
    public static final long DIAMOND_PRIZE_AMOUNT_THREE_BLUE = 40;
    public static final long DIAMOND_PRIZE_AMOUNT_THREE_WHITE = 20;
    public static final long DIAMOND_PRIZE_AMOUNT_THREE_GREEN = 8;
    public static final long DIAMOND_PRIZE_AMOUNT_TWO_GREEN = 8/100L;
    public static final long DIAMOND_PRIZE_AMOUNT_THREE_RED = 3;
    public static final long DIAMOND_PRIZE_AMOUNT_TWO_RED = 4/100L;
    public static final long DIAMOND_PRIZE_AMOUNT_FAIL = 0;

    public static final long DIAMOND_NEW_PRIZE_AMOUNT_THREE_RED = 10;
    public static final long DIAMOND_NEW_PRIZE_AMOUNT_THREE_GREEN = 1;
    public static final long DIAMOND_NEW_PRIZE_AMOUNT_THREE_WHITE = 3/100L;
    public static final long DIAMOND_NEW_PRIZE_AMOUNT_THREE_BLUE = 1/100L;
    private int[] items;
    private GroupItemType groupItemType;
    private long prizeAmount;
    private boolean isJackpot;
    private int mutil = 1;
    public GroupItem(int[] items) {
        this.items = items;
        groupItemType = getGroupItemType();
    }

    public GroupItem(int[] items, int mutil) {
        this.items = items;
        this.mutil = mutil;
        groupItemType = getGroupItemTypeNew();
    }
    public long getPrizeAmount() {
        return prizeAmount;
    }

    public boolean isJackpot() {
        return isJackpot;
    }

    private GroupItemType getGroupItemTypeNew() {
        groupItemType = GroupItemType.FAIL;
        prizeAmount = 0;
        if (isThreeRed()) {
            groupItemType = GroupItemType.THREE_RED;
            prizeAmount = DIAMOND_NEW_PRIZE_AMOUNT_THREE_RED;
            if(mutil == 100)
                isJackpot = true;
        } else if (isThreeGreen()) {
            groupItemType = GroupItemType.THREE_GREEN;
            prizeAmount = DIAMOND_NEW_PRIZE_AMOUNT_THREE_GREEN;
        } else if (isThreeWhite()) {
            groupItemType = GroupItemType.THREE_WHITE;
            prizeAmount = DIAMOND_NEW_PRIZE_AMOUNT_THREE_WHITE;
        } else if (isThreeBlue()) {
            groupItemType = GroupItemType.THREE_BLUE;
            prizeAmount = DIAMOND_NEW_PRIZE_AMOUNT_THREE_BLUE;
        } else if (isThreeViolet()) {
            groupItemType = GroupItemType.THREE_VIOLET;
            prizeAmount = DIAMOND_PRIZE_AMOUNT_FAIL;
        }
        return groupItemType;
    }

    private GroupItemType getGroupItemType() {
        groupItemType = GroupItemType.FAIL;
        prizeAmount = 0;
        if (isThreeYellow()) {
            groupItemType = GroupItemType.THREE_YELLOW;
            isJackpot = true;
        } else if (isThreeViolet()) {
            groupItemType = GroupItemType.THREE_VIOLET;
            prizeAmount = DIAMOND_PRIZE_AMOUNT_THREE_VIOLET;
        } else if (isThreeBlue()) {
            groupItemType = GroupItemType.THREE_BLUE;
            prizeAmount = DIAMOND_PRIZE_AMOUNT_THREE_BLUE;
        } else if (isThreeWhite()) {
            groupItemType = GroupItemType.THREE_BLUE;
            prizeAmount = DIAMOND_PRIZE_AMOUNT_THREE_WHITE;
        } else if (isThreeGreen()) {
            groupItemType = GroupItemType.THREE_BLUE;
            prizeAmount = DIAMOND_PRIZE_AMOUNT_THREE_GREEN;
        } else if (isThreeRed()) {
            groupItemType = GroupItemType.THREE_BLUE;
            prizeAmount = DIAMOND_PRIZE_AMOUNT_THREE_RED;
        } else if (isDoubleKind()) {
            groupItemType = GroupItemType.DOUBLE_KIND;
            prizeAmount = DIAMOND_PRIZE_AMOUNT_TWO_RED + DIAMOND_PRIZE_AMOUNT_TWO_GREEN;
//            log("------------isDoubleKind --->prizeAmount = "+prizeAmount);
        } else if (isTwoKind()) {
            int check = getKindOfTwoKind();
            if (check == ItemKind.RED) {
                prizeAmount = DIAMOND_PRIZE_AMOUNT_TWO_RED;
            } else if (check == ItemKind.GREEN) {
                prizeAmount = DIAMOND_PRIZE_AMOUNT_TWO_GREEN;
            }
        }
        return groupItemType;
    }

    private void log(String msg) {
        System.out.println("Diamond<<GroupItem>>" + msg);
    }

    private boolean isThreeYellow() {
        return items[0] == items[1] && items[1] == items[2] && items[0] == ItemKind.YELLOW;
    }

    private boolean isThreeViolet() {
        return isThreeOfAKind() && hasItem(ItemKind.VIOLET);
    }

    private boolean isThreeBlue() {
        return isThreeOfAKind() && hasItem(ItemKind.BLUE);
    }

    private boolean isThreeWhite() {
        return isThreeOfAKind() && hasItem(ItemKind.WHITE);
    }

    private boolean isThreeRed() {
        return isThreeOfAKindRed() && hasItem(ItemKind.RED);
    }

    private boolean isThreeGreen() {
        return isThreeOfAKind() && hasItem(ItemKind.GREEN);
    }

    private boolean hasItem(int itemValue) {
        return items[0] == itemValue || items[1] == itemValue || items[2] == itemValue;
    }

    private boolean isThreeOfAKindRed() {
        boolean check = false;
        if (items[0] == items[1] && items[1] == items[2]) {
            check = true;
        }
        return check;
    }

    private boolean isThreeOfAKind() {
        boolean check = false;
        if (items[0] == items[1] && items[1] == items[2]) {
            check = true;
        } else if (items[0] == items[1] && items[2] == ItemKind.YELLOW) {
            check = true;
        } else if (items[0] == items[2] && items[1] == ItemKind.YELLOW) {
            check = true;
        } else if (items[1] == items[2] && items[0] == ItemKind.YELLOW) {
            check = true;
        } else if (items[1] == items[2] && items[1] == ItemKind.YELLOW && items[0] != ItemKind.YELLOW) {
            check = true;
        } else if (items[0] == items[2] && items[0] == ItemKind.YELLOW && items[1] != ItemKind.YELLOW) {
            check = true;
        } else if (items[0] == items[1] && items[0] == ItemKind.YELLOW && items[2] != ItemKind.YELLOW) {
            check = true;
        }
        return check;
    }

    private boolean isDoubleKind() {
        return hasItem(ItemKind.RED) && hasItem(ItemKind.GREEN) && hasItem(ItemKind.YELLOW);
    }

    private boolean isTwoKind() {
        return (items[0] == items[1] && (items[0] == ItemKind.RED || items[0] == ItemKind.GREEN))
                || (items[0] == items[2] && (items[0] == ItemKind.RED || items[0] == ItemKind.GREEN))
                || (items[1] == items[2] && (items[1] == ItemKind.RED || items[1] == ItemKind.GREEN));
    }

    private int getKindOfTwoKind() {
        int check = 0;
        if ((items[0] == items[1] && items[0] == ItemKind.RED) || (items[0] == items[2] && items[0] == ItemKind.RED) || (items[1] == items[2] && items[1] == ItemKind.RED)) {
            check = ItemKind.RED;
        } else if ((items[0] == items[1] && items[0] == ItemKind.GREEN) || (items[0] == items[2] && items[0] == ItemKind.GREEN) || (items[1] == items[2] && items[1] == ItemKind.GREEN)) {
            check = ItemKind.GREEN;
        }
        return check;
    }
}
