package com.vinplay.api.constants;

public class TypeVinhDanhConstants {
    public static final String DAY = "DAY";
    public static final String WEEK = "WEEK";
    public static final String MONTH = "MONTH";
    public static final String YEAR = "YEAR";


    public static String getValueByKey(String key) {
        if (key.equals(DAY)) {
            return DAY;
        }
        if (key.equals(WEEK)) {
            return WEEK;
        }
        if (key.equals(MONTH)) {
            return MONTH;
        }
        if (key.equals(YEAR)) {
            return YEAR;
        }
        return "";
    }
}
