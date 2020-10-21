package com.hwj.tieba.resp;

public enum ResponseEnum {
    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    ILLEGAL_ARGUMENTS(2,"ILLEGAL_ARGUMENTS"),
    NEED_LOGIN(10,"NEED_LOGIN");

    private int code;
    private String desc;

    ResponseEnum(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
