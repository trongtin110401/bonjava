/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.modules.minigame.utils;

import java.util.Random;

/**
 *
 * @author tangdung
 */
public class RandomUtil {

    public static int randInt(int min, int max) {
        max += 1;
        Random rand = new Random();
        int i = rand.nextInt(max - min) + min;
        return i;
    }

    public static int randInt(int max) {
        Random random = new Random();
        return random.nextInt(max);
    }
    
    public static double randDouble (double min, double max){
        Random r = new Random();
        return min + (max - min) * r.nextDouble();
    }
}
