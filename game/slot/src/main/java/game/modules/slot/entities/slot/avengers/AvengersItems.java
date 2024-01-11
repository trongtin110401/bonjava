/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.avengers;

import java.util.Arrays;

public class AvengersItems {

    // Tổng có 11 loại Item với id từ 0 => 10. (Xem class AvengersItem)
    // Ma Trận này được sử dụng để cấu hình tỷ lệ % xuất hiện của Item trong Wheel
    // Mỗi hàng của Ma trận là tập hợp các tham số cấu hình của mỗi Item và tương ứng với thứ tự của Wheel. Ví dụ: dòng 1 tương ứng với Wheel 1
    // Mỗi giá trị của Ma trận tương ứng với số item mà wheel chứa.
    //      + Ví dụ: Ví trí [0, 0] có giá trị bằng 8, điều này có nghĩa là sẽ có 8 Item có Id = 0 trong tổng số Item có trong Wheel 1
    //      + Chạy hàm main() để hiểu rõ hơn
    private static int[][] config = new int[][]
            {
                    {8, 8, 0, 35, 10, 10, 12, 25, 75, 80, 85},
                    {8, 8, 50, 25, 10, 15, 20, 25, 50, 60, 65},
                    {8, 8, 30, 35, 10, 15, 20, 25, 50, 60, 65},
                    {8, 8, 25, 30, 20, 20, 20, 25, 50, 55, 60},
                    {8, 8, 0, 35, 10, 15, 20, 30, 65, 75, 85}
            };
    private AvengersWheel[] wheels = new AvengersWheel[5];

    public AvengersItems() {
        for (int wheelIndex = 0; wheelIndex < 5; ++wheelIndex) {
            this.wheels[wheelIndex] = new AvengersWheel();
            for (int j = 0; j < 11; ++j) {
                int k = 0;
                while (k < config[wheelIndex][j]) {
                    this.wheels[wheelIndex].addItem(AvengersItem.findItem((byte) j));
                    k++;
                }
            }
        }
    }

    public static void main(String[] args) {
        AvengersItems avengersItems = new AvengersItems();
        avengersItems.print();
    }

    public AvengersItem random(int wheelIndex) {
        return this.wheels[wheelIndex].random();
    }

    public void refundItem(AvengersItem item, int wheelIndex) {
        this.wheels[wheelIndex].addItem(item);
    }

    public void print() {
        Arrays.stream(wheels).forEach(avengersWheel -> {
            avengersWheel.print();
        });
    }
}

