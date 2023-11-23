package com.vinplay.ace.utils;

import com.vinplay.vbee.common.utils.VinPlayUtils;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class AceUtils {
    public static String generateKeySc(String token,String ticket_id ,long money) throws UnsupportedEncodingException, NoSuchAlgorithmException {
       return VinPlayUtils.getMD5Hash(token+ticket_id+money+VinPlayUtils.getMD5Hash("bongdasubasa"));
    }
}
