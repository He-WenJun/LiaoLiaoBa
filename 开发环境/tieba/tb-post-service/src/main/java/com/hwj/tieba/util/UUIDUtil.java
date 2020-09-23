package com.hwj.tieba.util;

import java.util.UUID;

public class UUIDUtil {
    public static String getStringUUID(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }

    public static void main(String[] args) {
        int a = 1;
        if(a == 1){
            System.out.println(1);
        }else if(a >0){
            System.out.println(1);
        }
    }
}
