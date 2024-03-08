/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line20extend;

import java.util.Arrays;

public class Slot20ExtendItems {

    // Tổng có 10 loại Item với id từ 0 => 9. (Xem class AvengersItem)
    // Ma Trận này được sử dụng để cấu hình tỷ lệ % xuất hiện của Item trong Wheel
    // Mỗi hàng của Ma trận là tập hợp các tham số cấu hình của mỗi Item và tương ứng với thứ tự của Wheel. Ví dụ: dòng 1 tương ứng với Wheel 1
    // Mỗi giá trị của Ma trận tương ứng với số item mà wheel chứa.
    //      + Ví dụ: Ví trí [0, 0] có giá trị bằng 8, điều này có nghĩa là sẽ có 8 Item có Id = 0 trong tổng số Item có trong Wheel 1
    //      + Chạy hàm main() để hiểu rõ hơn
//    private static final int[][] config = new int[][]
//            {
//                    {8, 8, 0, 35, 10, 10, 10, 12, 12, 25, 75, 75, 80, 80},
//                    {8, 8, 50, 0, 10, 15, 10, 20, 12, 25, 50, 75, 60, 60},
//                    {8, 8, 0, 35, 10, 15, 15, 20, 12, 25, 50, 75, 60, 60},
//                    {8, 8, 50, 0, 20, 20, 20, 20, 12, 25, 50, 75, 55, 55},
//                    {8, 8, 0, 35, 10, 15, 15, 20, 12, 30, 65, 75, 75, 75}
//            };


    private static final int[][] config = new int[][]
            {
                    {8, 8, 0, 35, 10, 10, 10, 12, 12, 25, 35, 40, 50, 55},
                    {8, 8, 15, 0, 10, 15, 10, 20, 12, 25, 30, 30, 50, 55},
                    {8, 8, 0, 35, 10, 15, 15, 20, 12, 25, 30, 40, 50, 55},
                    {8, 8, 15, 0, 20, 20, 20, 20, 12, 25, 30, 40, 50, 55},
                    {8, 8, 0, 35, 10, 15, 15, 20, 12, 30, 35, 40, 50, 55}
            };

    private final Slot20ExtendWheel[] wheels = new Slot20ExtendWheel[5];

    public Slot20ExtendItems() {
        for (int wheelIndex = 0; wheelIndex < 5; ++wheelIndex) {
            this.wheels[wheelIndex] = new Slot20ExtendWheel();
            for (int j = 0; j < 14; j++) {
                int k = 0;
                while (k < config[wheelIndex][j]) {
                    this.wheels[wheelIndex].addItem(Slot20ExtendItem.findItem((byte) j));
                    k++;
                }
            }
        }
    }

    public Slot20ExtendItem random(int wheelIndex) {
        return wheels[wheelIndex].random();
    }

    public void refundItem(Slot20ExtendItem item, int wheelIndex) {
        this.wheels[wheelIndex].addItem(item);
    }

    public void print() {
        Arrays.stream(wheels)
                .forEach(Slot20ExtendWheel::print);
    }
}

