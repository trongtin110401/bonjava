package com.vinplay.api.processors.hub777;

import com.google.gson.Gson;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AESEncryption {
    public static final String SECRETKEY = "2A96167EC7C69D62EA6464B71F9C5346";
    public static final String ENCRYPTIONKEY = "DD5D638AE70B57A66C74C4D35A26A545";
    public static final String SHA256KEY = "1m9hDzV5i4rC";
    public static final String GAMEID = "21";

//    public static void main(String args[]) throws Exception {
//        String key = ENCRYPTIONKEY;
//        System.out.println("key: " + key);
//        System.out.println("data: abc");
//        byte[] cipherText = encrypt("abc".getBytes(), key.getBytes()); // 32 length Key
//        System.out.println("result: " + new String(cipherText));
//        byte[] origText = decrypt(new String(cipherText).getBytes(), key.getBytes());
//        System.out.println(new String(origText));
//    }

    public static String encryptData(String data) {
        return new String(encrypt(data.getBytes(),ENCRYPTIONKEY.getBytes()));
    }
    public static byte[] encrypt(byte[] plainTextData, byte[] secretKey) {
        try {
            String iv = new String(secretKey).substring(0, 16);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

            byte[] dataBytes = plainTextData;
            int plaintextLength = dataBytes.length;
            byte[] plaintext = new byte[plaintextLength];
            System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);

            SecretKeySpec keyspec = new SecretKeySpec(secretKey, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);
            byte[] encrypted = cipher.doFinal(plaintext);

            return new String(Base64.getEncoder().encode(encrypted)).getBytes();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] decrypt(byte[] cipherTextData, byte[] secretKey) throws Exception {
        try {
            String iv = new String(secretKey).substring(0, 16);

            byte[] encrypted = Base64.getDecoder().decode(cipherTextData);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            SecretKeySpec keyspec = new SecretKeySpec(secretKey, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());

            cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

            byte[] original = cipher.doFinal(encrypted);
            String originalString = new String(original);
            return originalString.getBytes();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String computeSHA256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
            byte[] hashBytes = digest.digest(inputBytes);
            StringBuilder builder = new StringBuilder();

            for (byte b : hashBytes) {
                builder.append(String.format("%02x", b));
            }

            return builder.toString();
        } catch (Exception e) {
            // Handle the exception or rethrow it
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
//        String input = "YourInputString";
//        String hash = computeSHA256Hash(input);
//        System.out.println("SHA-256 Hash: " + hash);
//        getLinkUrl("21", "3142d7303886d8c096b1694ad6ad57d0");
//        System.out.println(new String(decrypt("bJZQb4HBR1yHMTRi0KK4po1uh8a1Jg6fs3gNfkLfPzEDVrStxB8aWmhWfy+OQGc4NAkEX/1VTDhDAbO3NAThWi5UjHYEqJ4YPjWEy8j/8Te0JBm/cvK8MJh0YPNwDdFzDVElC16YIgGGJzDmnMuHLw==".getBytes(), ENCRYPTIONKEY.getBytes())));
    }

    public static String getLinkUrl(String gameId, String token) {
//        3142d7303886d8c096b1694ad6ad57d0
        String time = new Date().getTime() + "";
        String data = "Method=0&Key=" + SECRETKEY + "&Time=" + time + "&Token=" + token;
        String encryptedData = new String(encrypt(data.getBytes(), ENCRYPTIONKEY.getBytes()));
        System.out.println(encryptedData);
        encryptedData = URLEncoder.encode(encryptedData);
        String checksum = computeSHA256Hash(data + SHA256KEY + time + SECRETKEY);
        checksum = URLEncoder.encode(checksum);
        if(gameId.equals("1001") || gameId.equals("1002")) {
            String url = "https://basefastgame.allwinslots.asia/?agent=9&encryptedData=" + encryptedData + "&checksum=" + checksum + "&gameid=" + gameId + "&deviceid=sdfsdfsdfsdf&ip=sdfsdfsdf&platform=dfasdfasdfsd&uitype=2";
            System.out.println(url);
            return url;
        }else {
            String url = "https://baseslot.allwinslots.asia/?agent=9&encryptedData=" + encryptedData + "&checksum=" + checksum + "&gameid=" + gameId + "&deviceid=sdfsdfsdfsdf&ip=sdfsdfsdf&platform=dfasdfasdfsd&uitype=2";
            System.out.println(url);
            return url;
        }
    }


    public static String getTokenFromRawData(String rawData) throws Exception {
        String data = new String(decrypt(rawData.getBytes(), ENCRYPTIONKEY.getBytes()));
        String token = bocTachRegex(data, "(Token=)(.*)", 2);
        return token;
    }

    public static String bocTachRegex(String raw, String regex, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(raw);
        if (matcher.find()) {
            String output = matcher.group(group);
            System.out.println(output);
            return output;
        }
        return "";
    }

    public static HashMap<String, String> dataStringToObject(String data) {
        HashMap<String, String> result = new HashMap<>();
        try {
            String rawData = new String(decrypt(data.getBytes(), ENCRYPTIONKEY.getBytes()));
            try {
                if (rawData.contains("&")) {
                    String[] dataObject = rawData.split("&");
                    for (String object : dataObject) {
                        String[] objectValue = object.split("=");
                        try {
                            result.put(objectValue[0], objectValue[1]);
                        }catch (Exception e) {
                            result.put(objectValue[0], "");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        } catch (Exception e) {
            e.getMessage();
        }
        return result;
    }
}
