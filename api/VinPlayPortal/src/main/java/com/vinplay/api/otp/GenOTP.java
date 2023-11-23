package com.vinplay.api.otp;

import java.util.Random;

public class GenOTP {

    public synchronized String GenOTP()
    {
        String otp = "";
        String numbers = "0123456789";
        String values = numbers;
        Random rndm_method = new Random();
        char[] password = new char[6];
        for (int i = 0; i < 6; i++)
        {
            password[i] = values.charAt(rndm_method.nextInt(values.length()));
        }
        for(char x : password){
            otp = otp+x;
        }
        return otp;
    }


}
