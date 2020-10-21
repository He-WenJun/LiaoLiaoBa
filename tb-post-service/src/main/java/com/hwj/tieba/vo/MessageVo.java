package com.hwj.tieba.vo;

import java.util.Date;

public class MessageVo<T> {
    private String id;
    private String message;
    private T data;
    private int typeId;
    private Date date;

    public MessageVo(){}
    public MessageVo(String id, String message, T data, int typeId, Date date) {
        this.id = id;
        this.message = message;
        this.data = data;
        this.typeId = typeId;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
