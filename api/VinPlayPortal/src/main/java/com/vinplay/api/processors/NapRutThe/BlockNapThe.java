package com.vinplay.api.processors.NapRutThe;

public class BlockNapThe {
    public boolean checkFormat(String seri, String pin, int type){
        int dai1 = seri.length();
        int dai2 = pin.length();

        if(type == 0 && dai1 == 14 && dai2 == 15){
            return true;
        }else if(type == 0 && dai1 == 11 && dai2 == 13){
            return true;
        }else if(type == 1 && dai1 == 14 && dai2 == 14){
            return true;
        }else if(type == 2 && dai1 == 15 && dai2 == 12){
            return true;
        }else{
            return false;
        }
    }




}
