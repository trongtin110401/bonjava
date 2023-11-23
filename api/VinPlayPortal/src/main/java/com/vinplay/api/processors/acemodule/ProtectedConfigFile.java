package com.vinplay.api.processors.acemodule;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Base64;

public class ProtectedConfigFile {
    public static final char[] PASSWORD = "mnahfana3vwdgdlksdsgm".toCharArray();
    private static final byte[] SALT = { (byte) 0x12, (byte) 0xde, (byte) 0x11, (byte) 0xdd, (byte) 0xd0, (byte) 0x32,
            (byte) 0x1c, (byte) 0xd2, };

    public static void main(String[] args) throws Exception {
        String ortherPass = "";
        String originalPassword = "dừvagasvddsfa dfas dfasdfjh slkdfjh salkdfjh lsadfjh laskdjfhlaksjdhf lkasjdhfiweuhfskdajhf dlfkajshdf ieuwhf lksdjfh ksdjfhfdsv";
        System.out.println("Original password: " + originalPassword);
        String encryptedPassword = encrypt(originalPassword);
        System.out.println("Encrypted password: " + encryptedPassword);
        String decryptedPassword = decrypt(encryptedPassword,PASSWORD);
        System.out.println("Decrypted password: " + decryptedPassword);
    }

    /**
     * MÃ hóa nó
     * @param property
     * @return
     * @throws GeneralSecurityException
     * @throws UnsupportedEncodingException
     */
    public static String encrypt(String property, char[] password) throws GeneralSecurityException, UnsupportedEncodingException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(SALT, 16));
        return base64Encode(pbeCipher.doFinal(property.getBytes("UTF-8")));
    }

    public static String encrypt(String data) {
        try {
            return encrypt(data,PASSWORD);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String base64Encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Giải mã nó
     * @param property
     * @return
     * @throws GeneralSecurityException
     * @throws IOException
     */
    public static String decrypt(String property,char[] password) throws GeneralSecurityException, IOException {
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey key = keyFactory.generateSecret(new PBEKeySpec(password));
        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(SALT, 16));
        return new String(pbeCipher.doFinal(base64Decode(property)), "UTF-8");
    }

    public static byte[] base64Decode(String property) throws IOException {
        return Base64.getDecoder().decode(property);
    }
}
