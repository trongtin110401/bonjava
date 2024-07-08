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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GenerationTaiXiu {
    private List<String> listCau = new ArrayList<String>();
    private CauTaiXiu cauTX = null;

    public void readConfig() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(VBeePath.basePath + "config/taixiu.dat"));
        String str = null;
        while ((str = reader.readLine()) != null) {
            if (str.isEmpty()) continue;
            this.listCau.add(str);
        }
    }

    public short[] generateResult(short forceBetSide) {
        short[] dices;
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
                Debug.trace("gd= " + this.cauTX.data);
                result = this.cauTX.getResultTX();
            } else {
                dices = this.generateDices();
                if ((dices[0] + dices[1] + dices[2]) == 18 || (dices[0] + dices[1] + dices[2]) == 3)
                    return this.generateResult(forceBetSide);
                return dices;
            }
        }
        if (result == -1) {
            dices = this.generateDices();
            if ((dices[0] + dices[1] + dices[2]) == 18 || (dices[0] + dices[1] + dices[2]) == 3)
                return this.generateResult(forceBetSide);
            return dices; // đoạn này check mới cho return;
        }
        while (((dices = this.generateDices())[0] + dices[1] + dices[2] > 10 ? 1 : 0) != result) {
        }
        if ((dices[0] + dices[1] + dices[2]) == 18 || (dices[0] + dices[1] + dices[2]) == 3)
            return this.generateResult(forceBetSide);
        return dices;
    }

    public short[] generateDices() {
        short[] dices = new short[3];
        dices[0] = (short) (ThreadLocalRandom.current().nextInt(6) + 1);
        dices[1] = (short) (ThreadLocalRandom.current().nextInt(6) + 1);
        dices[2] = (short) (ThreadLocalRandom.current().nextInt(6) + 1);
        return dices;
    }

    public String buildPlainTextResult(short[] dices) {
        return generateRandomString(10 + ThreadLocalRandom.current().nextInt(10) + 1)
                + "{" + dices[0] + "-" + dices[1] + "-" + dices[2] + "}" +
                generateRandomString(10 + ThreadLocalRandom.current().nextInt(10) + 1);
    }

    public static String hashMD5(String input) {
        try {
            // Create an MD5 message digest
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Update the message digest with the input bytes
            md.update(input.getBytes());

            // Get the hash value as an array of bytes
            byte[] digest = md.digest();

            // Convert the byte array to a hexadecimal string
            StringBuilder result = new StringBuilder();
            for (byte b : digest) {
                result.append(String.format("%02x", b));
            }

            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle the exception, e.g., log it or throw a custom exception
            e.printStackTrace();
            return null;
        }
    }

    public static String generateRandomString(int length) {
        // Define the characters that can be used in the random string
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+";

        // Create a StringBuilder to build the random string
        StringBuilder randomStringBuilder = new StringBuilder();

        // Create a Random object
        Random random = new Random();

        // Generate the random string of the specified length
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomStringBuilder.append(randomChar);
        }

        // Convert StringBuilder to String and return
        return randomStringBuilder.toString();
    }

    public short[] generateDiceNoHu(short forceBetSide) {
        short[] dices = new short[3];
        if (forceBetSide == 1) {
            dices[0] = dices[1] = dices[2] = 6;
        } else {
            dices[0] = dices[1] = dices[2] = 1;
        }
        return dices;
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

