package com.randal.aviana;


public class FileUtil {
    private static final int UNIT_1024 = 1024;
    private static final int UNIT_BYTE_TO_M = 1048576; // (1024 * 1024)
    private static final String UNIT_M = "M";
    private static final String UNIT_G = "G";

    public static String byteToUnit(long bytes) {
        String result = "";
        String unit = "";
        float mb = 1.0f * bytes / UNIT_BYTE_TO_M;
        if (mb > UNIT_1024) {
            unit = UNIT_G;
            mb /= UNIT_1024;
        } else {
            unit = UNIT_M;
        }
        result = (int) mb + unit;
        return result;
    }
}
