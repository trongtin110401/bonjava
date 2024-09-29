/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25basic;

import java.util.Arrays;

public class Slot25BasicItems {

    // Tổng có 11 loại Item với id từ 0 => 10. (Xem class AvengersItem)
    // Ma Trận này được sử dụng để cấu hình tỷ lệ % xuất hiện của Item trong Wheel
    // Mỗi hàng của Ma trận là tập hợp các tham số cấu hình của mỗi Item và tương ứng với thứ tự của Wheel. Ví dụ: dòng 1 tương ứng với Wheel 1
    // Mỗi giá trị của Ma trận tương ứng với số item mà wheel chứa.
    //      + Ví dụ: Ví trí [0, 0] có giá trị bằng 8, điều này có nghĩa là sẽ có 8 Item có Id = 0 trong tổng số Item có trong Wheel 1
    //      + Chạy hàm main() để hiểu rõ hơn
    private static int[][] config = new int[][]
            {
                    {8, 10, 0, 5, 10, 15, 20, 25, 30, 35, 40},
                    {8, 10, 5, 5, 40, 35, 30, 25, 20, 15, 10},
                    {8, 10, 5, 5, 10, 15, 20, 25, 30, 35, 40},
                    {8, 10, 5, 5, 40, 35, 30, 25, 20, 15, 10},
                    {8, 10, 0, 5, 10, 15, 20, 25, 30, 35, 40}
            };

//    private static int[][] config = new int[][]
//            {
//                    {20, 10, 0, 35, 10, 10, 12, 25, 75, 80, 85},
//                    {10, 10, 50, 35, 10, 15, 20, 25, 50, 60, 65},
//                    {10, 10, 50, 35, 10, 15, 20, 25, 50, 60, 65},
//                    {10, 10, 50, 35, 20, 20, 20, 25, 50, 55, 60},
//                    {20, 40, 0, 35, 10, 15, 20, 30, 65, 75, 85}
//            };

    private Slot25BasicWheel[] wheels = new Slot25BasicWheel[5];

    public Slot25BasicItems() {
        for (int wheelIndex = 0; wheelIndex < 5; ++wheelIndex) {
            this.wheels[wheelIndex] = new Slot25BasicWheel();
            for (int j = 0; j < config[0].length; j++) {
                int k = 0;
                while (k < config[wheelIndex][j]) {
                    this.wheels[wheelIndex].addItem(SlotBasic25Item.findItem((byte) j));
                    k++;
                }
            }
        }
    }

    public static void main(String[] args) {
        Slot25BasicItems avengersItems = new Slot25BasicItems();
        avengersItems.print();
    }

    public SlotBasic25Item random(int wheelIndex) {
        return wheels[wheelIndex].random();
    }

    public void refundItem(SlotBasic25Item item, int wheelIndex) {
        this.wheels[wheelIndex].addItem(item);
    }

    public void print() {
        Arrays.stream(wheels).forEach(avengersWheel -> {
            avengersWheel.print();
        });
    }
}

