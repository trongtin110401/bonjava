
package game.modules.slot.utils;

import game.modules.slot.entities.slot.Line;
import game.modules.slot.entities.slot.line25basic.SlotBasic25Item;
import game.modules.slot.entities.slot.line25basic.SlotHalloweenAward;
import game.modules.slot.entities.slot.line25basic.SlotHalloweenAwards;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlotHalloweenUtil extends Slot25BasicUtil {

    /**
     * Phương thức này được sử dụng để tính toán giải thưởng cho số lần xuất hiện của ITEM trên 1 LINE
     * mà không bao gồm việc tính toán số lần quay miễn phí và giải thưởng cho BONUS game
     */

    public static void calculateMoneyAwardInLine2(Line line, List<SlotHalloweenAward> awardList) {
        // kiểm tra jackpot trước
        List cels = line.getCells();
        if (cels.get(0) == SlotBasic25Item.JACKPOT
                && (cels.get(1) == SlotBasic25Item.JACKPOT)
                && cels.get(2) == SlotBasic25Item.JACKPOT
                && cels.get(3) == SlotBasic25Item.JACKPOT
                && cels.get(4) == SlotBasic25Item.JACKPOT) {
            awardList.add(SlotHalloweenAward.PENTA_JACKPOT);
            return;
        }

        // Duyệt mảng từ trái sang phải
        Map<Byte, Integer> item2Count = new HashMap<>();
        for (int i = 0; i < line.getCells().size(); i++) {
            int count = 1; // Biến đếm số lượng trùng lặp
            SlotBasic25Item currentItem = (SlotBasic25Item) line.getCell(i).getItem(); // item hiện tại
            // Bỏ qua không đếm do các items này không có phần thưởng hệ số
            if (currentItem == SlotBasic25Item.SCATTER || currentItem == SlotBasic25Item.BONUS) {
                continue;
            }
            // So sánh item hiện tại với item tiếp theo
            for (int j = i + 1; j < line.getCells().size(); j++) {
                SlotBasic25Item nextItem = (SlotBasic25Item) line.getCell(j).getItem();
                if (currentItem == nextItem) {
                    count += 1;
                } else if (nextItem == SlotBasic25Item.WILD && currentItem != SlotBasic25Item.JACKPOT) {
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
            SlotBasic25Item item = SlotBasic25Item.findItem(id);
            SlotHalloweenAward award = SlotHalloweenAwards.getAward(item, countNumItem);
            if (award != null) {
                awardList.add(award);
            }
        });
    }
}

