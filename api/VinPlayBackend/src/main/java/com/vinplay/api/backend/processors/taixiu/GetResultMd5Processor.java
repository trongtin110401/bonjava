package com.vinplay.api.backend.processors.taixiu;

import com.vinplay.dal.service.impl.TaiXiuMd5ServiceImpl;
import com.vinplay.vbee.common.cp.BaseProcessor;
import com.vinplay.vbee.common.cp.Param;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class GetResultMd5Processor implements BaseProcessor<HttpServletRequest, String> {
    @Override
    public String execute(Param<HttpServletRequest> param) {
        String response = null;
        try {
            HttpServletRequest request = param.get();
            String plainText = request.getParameter("plainText");
            TaiXiuMd5ServiceImpl taiXiuMd5Service = new TaiXiuMd5ServiceImpl();
            response = taiXiuMd5Service.getHashMd5(plainText);
            if (response == null) {
                response = hashMD5(plainText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;

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
}
