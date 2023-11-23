/*
 * Decompiled with CFR 0.144.
 */
package game.modules.slot.entities.slot.ndv;

import game.modules.slot.entities.slot.ndv.NDVItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NDVItems {
    private static int[] config = new int[]{30, 12, 32, 50, 60, 60, 69, 75, 80};    
    public List<NDVItem> items = new ArrayList<NDVItem>();

    public NDVItems() {
        for (int i = 0; i < config.length; ++i) {
            for (int j = 0; j < config[i]; ++j) {
                this.items.add(NDVItem.findItem((byte)i));
            }
        }
    }

    public int size() {
        return this.items.size();
    }

    public NDVItem random() {
        Random rd = new Random();
        int index = rd.nextInt(this.items.size());
        return this.items.get(index);
    }
//    private static int[][] config100 = new int[][]{{25, 30, 10, 55, 50, 65, 70, 70, 80}, {25, 30, 10, 60, 60, 60, 69, 75, 80}, 
//        {25, 30, 10, 55, 50, 65, 65, 70, 75}, {25, 30, 10, 50, 60, 70, 75, 55, 65}, {25, 30, 10, 50, 50, 50, 60, 65, 70}};
//    
//    private static int[][] config1000 = new int[][]{{25, 30, 10, 55, 50, 65, 70, 70, 80}, {25, 30, 10, 60, 60, 60, 69, 75, 80}, 
//        {25, 30, 10, 55, 50, 65, 65, 70, 75}, {25, 30, 10, 50, 60, 70, 75, 55, 65}, {25, 30, 10, 50, 50, 50, 60, 65, 70}};
//    
//    private static int[][] config10000 = new int[][]{{25, 30, 10, 55, 50, 65, 70, 70, 80}, {25, 30, 10, 60, 60, 60, 69, 75, 80}, 
//        {25, 30, 10, 55, 50, 65, 65, 70, 75}, {25, 30, 10, 50, 60, 70, 75, 55, 65}, {25, 30, 10, 50, 50, 50, 60, 65, 70}};
//    private NDVWheel[] wheels = new NDVWheel[5];
//
//    public NDVItems(int room) {
//        if (room == 100)
//        {
//            for (int i = 0; i < 5; ++i) {
//                this.wheels[i] = new NDVWheel();
//                for (int j = 0; j < 9; ++j) {
//
//                    for (int k = 0; k < config100[i][j]; ++k) {
//                        this.wheels[i].addItem(NDVItem.findItem((byte) j));
//                    }
//                }
//            }
//        }
//        else if (room == 1000)
//        {
//            for (int i = 0; i < 5; ++i) {
//                this.wheels[i] = new NDVWheel();
//                for (int j = 0; j < 9; ++j) {
//
//                    for (int k = 0; k < config1000[i][j]; ++k) {
//                        this.wheels[i].addItem(NDVItem.findItem((byte) j));
//                    }
//                }
//            }
//        }
//        else
//        {
//            for (int i = 0; i < 5; ++i) {
//                this.wheels[i] = new NDVWheel();
//                for (int j = 0; j < 9; ++j) {
//
//                    for (int k = 0; k < config10000[i][j]; ++k) {
//                        this.wheels[i].addItem(NDVItem.findItem((byte) j));
//                    }
//                }
//            }
//        }
//    }
//
//    public NDVItem random(int wheel) {
//        return this.wheels[wheel].random();
//    }
//
//    public void refundItem(NDVItem item, int wheel) {
//        this.wheels[wheel].addItem(item);
//    }
}

