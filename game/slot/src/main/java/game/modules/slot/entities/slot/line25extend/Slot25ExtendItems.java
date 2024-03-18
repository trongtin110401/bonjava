/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.line25extend;

import java.util.Arrays;

public class Slot25ExtendItems {

    // Tổng có 11 loại Item với id từ 0 => 10. (Xem class AvengersItem)
    // Ma Trận này được sử dụng để cấu hình tỷ lệ % xuất hiện của Item trong Wheel
    // Mỗi hàng của Ma trận là tập hợp các tham số cấu hình của mỗi Item và tương ứng với thứ tự của Wheel. Ví dụ: dòng 1 tương ứng với Wheel 1
    // Mỗi giá trị của Ma trận tương ứng với số item mà wheel chứa.
    //      + Ví dụ: Ví trí [0, 0] có giá trị bằng 8, điều này có nghĩa là sẽ có 8 Item có Id = 0 trong tổng số Item có trong Wheel 1
    //      + Chạy hàm main() để hiểu rõ hơn
//    private static int[][] config = new int[][]
//            {
//                    {55, 50, 0, 35, 10, 10, 12, 25, 75, 80, 85},
//                    {55, 50, 20, 25, 10, 15, 20, 25, 50, 60, 65},
//                    {55, 50, 20, 35, 10, 15, 20, 25, 50, 60, 65},
//                    {55, 50, 20, 30, 20, 20, 20, 25, 50, 55, 60},
//                    {55, 50, 0, 35, 10, 15, 20, 30, 65, 75, 85}
//            };


    private static int[][] config = new int[][]
            {
                    {40, 40, 0, 35, 10, 10, 12, 25, 75, 30, 25},
                    {40, 40, 50, 35, 10, 15, 20, 25, 50, 30, 25},
                    {40, 40, 50, 35, 10, 15, 20, 25, 50, 30, 25},
                    {40, 40, 55, 35, 20, 20, 20, 25, 50, 35, 20},
                    {40, 40, 0, 35, 10, 15, 20, 30, 65, 35, 25}
            };

    private Slot25ExtendWheel[] wheels = new Slot25ExtendWheel[5];

    public Slot25ExtendItems() {
        for (int wheelIndex = 0; wheelIndex < 5; ++wheelIndex) {
            this.wheels[wheelIndex] = new Slot25ExtendWheel();
            for (int j = 0; j < 11; j++) {
                int k = 0;
                while (k < config[wheelIndex][j]) {
                    this.wheels[wheelIndex].addItem(Slot25ExtendItem.findItem((byte) j));
                    k++;
                }
            }
        }
    }

    public static void main(String[] args) {
        Slot25ExtendItems avengersItems = new Slot25ExtendItems();
        avengersItems.print();
    }

    public Slot25ExtendItem random(int wheelIndex) {
        return wheels[wheelIndex].random();
    }

    public void refundItem(Slot25ExtendItem item, int wheelIndex) {
        this.wheels[wheelIndex].addItem(item);
    }

    public void print() {
        Arrays.stream(wheels).forEach(avengersWheel -> {
            avengersWheel.print();
        });
    }
}

