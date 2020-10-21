package com.hwj.tieba.util;

import java.util.UUID;

public class UUIDUtil {
    public static String getStringUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

}
