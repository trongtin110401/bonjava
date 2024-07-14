/*
 * Decompiled with CFR 0.144.
 *
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonProcessingException
 *  com.fasterxml.jackson.databind.ObjectMapper
 */
package com.vinplay.vbee.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vinplay.vbee.common.models.UserClientInfo;
import com.vinplay.vbee.common.pools.ConnectionPool;
import com.vinplay.vbee.common.statics.TimeBasedOneTimePasswordUtil;
import com.vinplay.vbee.common.utils.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import jdk.nashorn.internal.runtime.Debug;
import org.apache.log4j.Logger;

public class VinPlayUtils {
    private static Set<String> generatedCode = new HashSet<String>();
    private static long oldTransId = 0L;
    private static final Logger logger = Logger.getLogger((String) "api");

    public static String getMD5Hash(String value) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        byte[] digest;
        StringBuilder sbMd5Hash = new StringBuilder();
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(value.getBytes("UTF-8"));
        byte[] data = digest = m.digest();
        for (byte element : digest) {
            sbMd5Hash.append(Character.forDigit(element >> 4 & 15, 16));
            sbMd5Hash.append(Character.forDigit(element & 15, 16));
        }
        return sbMd5Hash.toString();
    }

    public static String hmacDigest(String msg, String keyString, String algo) throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String digest = null;
        SecretKeySpec key = new SecretKeySpec(keyString.getBytes("UTF-8"), algo);
        Mac mac = Mac.getInstance(algo);
        mac.init(key);
        byte[] bytes = mac.doFinal(msg.getBytes("ASCII"));
        StringBuffer hash = new StringBuffer();
        for (int i = 0; i < bytes.length; ++i) {
            String hex = Integer.toHexString(255 & bytes[i]);
            if (hex.length() == 1) {
                hash.append('0');
            }
            hash.append(hex);
        }
        digest = hash.toString();
        return digest;
    }

    public static Date subtractDay(Date date, int pre) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(5, -pre);
        return cal.getTime();
    }

    public static int compareDate(Date date1, Date date2) throws ParseException {
        long time2;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long time1 = df.parse(VinPlayUtils.parseDateToString(date1)).getTime();
        if (time1 == (time2 = df.parse(VinPlayUtils.parseDateToString(date2)).getTime())) {
            return 0;
        }
        if (time1 > time2) {
            return 1;
        }
        return -1;
    }

    public static String parseDateToString(Date input) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(input);
    }

    public static String parseDateTimeToString(Date input) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(input);
    }

    public static String getCurrentDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    public static String getYesterday() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.add(Calendar.DATE, -1);
        return df.format(aCalendar.getTime());
    }

    public static String getTomorrowString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(getTomorrow());
    }

    public static Date getTomorrow() {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.add(Calendar.DATE, 1);
        return aCalendar.getTime();
    }

    public static Date getCurrentDates() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(df.format(new Date()));
    }

    public static String getCurrentDateMarketing() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(new Date());
    }

    public static String getCurrentDateTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }


    public static String getYesterday1() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.add(5, -1);
        return df.format(aCalendar.getTime());
    }

    public static String get5MinAgoYesterday1() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.add(Calendar.MINUTE, -5);
        return df.format(aCalendar.getTime());
    }

    public static String getpreterday1() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.add(5, -20);
        return df.format(aCalendar.getTime());
    }

    public static String getSessionDate() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar aCalendar = Calendar.getInstance();

        return df.format(aCalendar.getTime());
    }

    public static Date getDateTime(String datetime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.parse(datetime);
    }

    public static Calendar getCalendar(String datetime) throws ParseException {
        if (datetime != null && !datetime.isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            calendar.setTime(format.parse(datetime));
            return calendar;
        }
        return null;
    }

    public static String getDateTimeStr(Date datetime) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(datetime);
    }

    public static Date getDateTimeFromDate(String date) throws ParseException {
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        return formatDate.parse(date);
    }

    public static String getDateFromDateTime(String datetime) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(VinPlayUtils.getDateTime(datetime));
    }

    public static String formatDateTime(String strDateTime) {
        return strDateTime.substring(8, 10) + "/" + strDateTime.substring(5, 7) + "/" + strDateTime.substring(0, 4) + strDateTime.substring(10);
    }

    public static String formatDate(String strDateTime) {
        return strDateTime.substring(0, 4) + "-" + strDateTime.substring(5, 7) + "-" + strDateTime.substring(8, 10) + " " + strDateTime.substring(11, 13) + ":" + strDateTime.substring(14, 16) + ":" + strDateTime.substring(17, 19);
    }

    public static String genSessionId(int userId, String gameName) {
        return String.valueOf(userId) + gameName + String.valueOf(System.currentTimeMillis());
    }

    public static String genMessageId() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String genTransactionId(int userId) {
        return String.valueOf(userId) + String.valueOf(System.currentTimeMillis());
    }

    public static synchronized long generateTransId() {
        long transId = System.currentTimeMillis();
        while (transId == oldTransId) {
            transId = System.currentTimeMillis();
        }
        oldTransId = transId;
        return oldTransId;
    }

    public static String genSessionKey(UserClientInfo userInfo) throws JsonProcessingException, UnsupportedEncodingException {
        ObjectMapper mapper = new ObjectMapper();
        return VinPlayUtils.encodeBase64(mapper.writeValueAsString((Object) userInfo));
    }

    public static String genAccessToken(int userId) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return VinPlayUtils.getMD5Hash(String.valueOf(userId) + "@VinPlay#6102$817" + System.currentTimeMillis());
    }

    public static boolean sessionTimeout(long recentTime) {
        return new Date().getTime() - recentTime > 10800000L;
    }

    public static boolean cashoutBlockTimeout(Date recentTime, int hoursBlock) {
        return new Date().getTime() - recentTime.getTime() > (long) (hoursBlock * 60 * 60 * 1000);
    }

    public static boolean socialTimeout(Date recentTime) {
        return new Date().getTime() - recentTime.getTime() > 86400000L;
    }

    public static boolean emailTimeout(Date emailTime) {
        return new Date().getTime() - emailTime.getTime() > 86400000L;
    }

    public static boolean updateMoneyTimeout(long activeTime, int timeOut) {
        return new Date().getTime() - activeTime > ((long) timeOut * 60 * 1000);
    }

    public static boolean isAlertTimeout(Date alertTime, int timeOut) {
        return alertTime == null || new Date().getTime() - alertTime.getTime() > (long) (timeOut * 60 * 1000);
    }

    public static String encodeBase64(String message) throws UnsupportedEncodingException {
        return DatatypeConverter.printBase64Binary(message.getBytes("UTF-8"));
    }

    public static String decodeBase64(String messageBase64) throws UnsupportedEncodingException {
        return new String(DatatypeConverter.parseBase64Binary(messageBase64), "UTF-8");
    }

    public static String genOtpApp(String nickname, String mobile) throws Exception {
        String secret = VinPlayUtils.getUserSecretKey(nickname);
        return TimeBasedOneTimePasswordUtil.generateCurrentNumberString(secret);
    }

    public static boolean removeUserSecret(String nickname) {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "DELETE FROM vinplay.user_appotp where nick_name = ?";
            PreparedStatement stm = conn.prepareStatement(sql);
            stm.setString(1, nickname);

            stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            return false;
        }
        return true;
    }

    public static boolean setUserSecretKey(String nickname, String secret) throws Exception {
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "INSERT INTO vinplay.user_appotp (nick_name,secret) VALUES(?,?)";
            PreparedStatement stm = conn.prepareStatement("INSERT INTO vinplay.user_appotp (nick_name,secret) VALUES(?,?)");
            stm.setString(1, nickname);
            stm.setString(2, secret);
            int rs = stm.executeUpdate();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            throw e;
        }
        return true;
    }

    public static String getUserSecretKey(String nickname) {
        String secret = "";
        Connection conn = ConnectionPool.getInstance().getConnection("mysqlpoolname");
        try {
            String sql = "SELECT * FROM user_appotp WHERE nick_name=?";
            PreparedStatement stm = conn.prepareStatement("SELECT * FROM user_appotp WHERE nick_name=?");
            stm.setString(1, nickname);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                secret = rs.getString("secret");
            }
            rs.close();
            stm.close();
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException sql) {
            // empty catch block
        }
        return secret;
    }

    public static String genOtpSMS(String mobile, String service) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return StringUtils.randomStringNumber(5);
    }

    public static boolean checkOtpTimeout(Date otpTime) {
        return new Date().getTime() - otpTime.getTime() > 300000L;
    }

    public static boolean checkSecurityTimeout(Date securityTime) {
        return new Date().getTime() - securityTime.getTime() > 86400000L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static String genGiftCode(int length) {

        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomString = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            randomString.append(randomChar);
        }

        return randomString.toString();
    }


    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadGiftcode(List<String> allGiftcode) {
        for (String gift : allGiftcode) {
            Set<String> set = generatedCode;
            synchronized (set) {
                if (generatedCode.contains(gift)) {
                    continue;
                }
                generatedCode.add(gift);
            }
        }
    }

    public static void main(String[] args) {

    }

    public static String mapProviderByNumber(String number) {
        if (number == null) {
            return "null";
        }
        if (number.matches("^(096|097|098|0162|0163|0164|0165|0166|0167|0168|0169|086|082)[\\d]{7}$")) {
            return "vtt";
        }
        if (number.matches("^(091|094|0123|0124|0125|0127|0129|088)[\\d]{7}$")) {
            return "vnp";
        }
        if (number.matches("^(090|093|0120|0121|0122|0126|0128|089)[\\d]{7}$")) {
            return "vms";
        }
        if (number.matches("^(092|0188|0186)[\\d]{7}$")) {
            return "vnm";
        }
        if (number.matches("^(099|0199)[\\d]{7}$")) {
            return "gtel";
        }
        return "other";
    }

    public static String getServiceName(String actionName) {
        switch (actionName) {
            case "Sam": {
                return "Ch\u01a1i game S\u00e2m";
            }
            case "BaCay": {
                return "Ch\u01a1i game Ba C\u00e2y";
            }
            case "Binh": {
                return "Ch\u01a1i game M\u1eadu Binh";
            }
            case "Tlmn": {
                return "Ch\u01a1i game TLMN";
            }
            case "TaLa": {
                return "Ch\u01a1i game T\u00e1 L\u1ea3";
            }
            case "Lieng": {
                return "Ch\u01a1i game Li\u00eang";
            }
            case "XiTo": {
                return "Ch\u01a1i game X\u00ec T\u1ed1";
            }
            case "BaiCao": {
                return "Ch\u01a1i game B\u00e0i C\u00e0o";
            }
            case "Poker": {
                return "Ch\u01a1i game Poker";
            }
            case "XocDia": {
                return "Ch\u01a1i game X\u00f3c \u0110\u00eda";
            }
            case "XiDzach": {
                return "Ch\u01a1i game X\u00ec D\u00e1ch";
            }
            case "Caro": {
                return "Ch\u01a1i game C\u1edd Caro";
            }
            case "CoTuong": {
                return "Ch\u01a1i game C\u1edd T\u01b0\u1edbng";
            }
            case "CoVua": {
                return "Ch\u01a1i game C\u1edd Vua";
            }
            case "PokerTour": {
                return "Ch\u01a1i Poker Tour";
            }
            case "CoUp": {
                return "Ch\u01a1i game C\u1edd \u00dap";
            }
        }
        return "Ch\u01a1i game " + actionName;
    }

    public static String genMailId(int len) {
        String strRandom = String.valueOf(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < len; ++i) {
            sb.append(strRandom.charAt(random.nextInt(strRandom.length())));
        }
        return sb.toString();
    }

    public static boolean isMobile(String platform) {
        return platform != null && !platform.equals("web");
    }
}

