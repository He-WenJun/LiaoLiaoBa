package com.hwj.tieba.resp;

public class ServerResponse<T>{
    private int status;
    private String msg;
    private T data;

    public ServerResponse(){}

    private ServerResponse(int status){
        this.status = status;
    }
    private ServerResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
    private ServerResponse(int status,T data){
        this.status = status;
        this.data = data;
    }
    private ServerResponse(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }


    /**
     * 成功的方法
     */
    public static <T>ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getCode(),ResponseEnum.SUCCESS.getDesc());
    }
    public static <T>ServerResponse<T> createBySuccessMessage(String message){
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getCode(),message);
    }
    public static <T>ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getCode(),data);
    }
    public static <T>ServerResponse<T> createBySuccess(String message, T data){
        return new ServerResponse<T>(ResponseEnum.SUCCESS.getCode(),message,data);
    }

    /**
     * 失败的方法
     */
    public static <T>ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResponseEnum.ERROR.getCode(),ResponseEnum.ERROR.getDesc());
    }
    public static <T>ServerResponse<T> createByErrorMessage(String msg){
        return new ServerResponse<T>(ResponseEnum.ERROR.getCode(),msg);
    }
    public static <T>ServerResponse<T> createByErrorCodeMessage(int code, String msg){
        return new ServerResponse<T>(code,msg);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
