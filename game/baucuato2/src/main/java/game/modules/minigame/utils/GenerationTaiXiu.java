/*
 * Decompiled with CFR 0.144.
 * 
 * Could not load the following classes:
 *  bitzero.util.common.business.Debug
 */
package game.modules.minigame.utils;

import bitzero.util.common.business.Debug;
import com.vinplay.vbee.common.config.VBeePath;
import game.utils.ConfigGame;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GenerationTaiXiu {
    private List<String> listCau = new ArrayList<String>();
    private CauTaiXiu cauTX = null;

    public void readConfig() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(VBeePath.basePath+"config/taixiu.dat"));
        String str = null;
        while ((str = reader.readLine()) != null) {
            if (str.isEmpty()) continue;
            this.listCau.add(str);
        }
    }

    public short[] generateResult(short forceBetSide) {
        int genResult;
        short[] dices;
        int totalDices;
        int result = forceBetSide;
        Random rd = new Random();
        if (this.cauTX != null && result == -1) {
            result = this.cauTX.getResultTX();
        }
        if (result == -1) {
            this.cauTX = null;
            int n = rd.nextInt(ConfigGame.getIntValueBaseMin("tx_num_rd", 1));
            if (n == 0 && this.listCau.size() > 0) {
                int cau = rd.nextInt(this.listCau.size());
                this.cauTX = new CauTaiXiu();
                String data = this.listCau.get(cau);
                this.cauTX.setData(data);
                Debug.trace((Object)("gd= " + this.cauTX.data));
                result = this.cauTX.getResultTX();
            } else {
                return this.generateDices();
            }
        }
        if (result == -1) {
            return this.generateDices();
        }
        while ((genResult = (totalDices = (dices = this.generateDices())[0] + dices[1] + dices[2]) > 10 ? 1 : 0) != result) {
        }
        return dices;
    }

    private short[] generateDices() {
        short[] dices = new short[3];
        dices[0] = (short)(ThreadLocalRandom.current().nextInt(6) + 1);
        dices[1] = (short)(ThreadLocalRandom.current().nextInt(6) + 1);
        dices[2] = (short)(ThreadLocalRandom.current().nextInt(6) + 1);
        return dices;
    }

    public static void main(String[] agrs) throws IOException {
    }

    private class CauTaiXiu {
        public int index = 0;
        private String data;

        private CauTaiXiu() {
        }

        public void setData(String data) {
            this.data = data;
            this.index = data.length() - 1;
        }

        public int getResultTX() {
            if (this.index < 0 || this.index >= this.data.length()) {
                return -1;
            }
            int result = Integer.parseInt("" + this.data.charAt(this.index));
            --this.index;
            return result;
        }
    }

}

