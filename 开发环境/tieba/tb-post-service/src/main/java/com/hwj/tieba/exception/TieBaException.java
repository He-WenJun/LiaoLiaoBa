package com.hwj.tieba.exception;

import com.hwj.tieba.resp.ResponseEnum;

public class TieBaException extends RuntimeException {
    private int exceptionStatus = ResponseEnum.ERROR.getCode();

    public int getExceptionStatus() {
        return exceptionStatus;
    }

    public void setExceptionStatus(int exceptionStatus) {
        this.exceptionStatus = exceptionStatus;
    }

    public TieBaException(String msg){
        super(msg);
    }

    public TieBaException(int code,String msg){
        super(msg);
        exceptionStatus = code;
    }
}
