package com.hwj.tieba.entity;

import java.util.Date;

public class PrivateMessage {
    /**发送消息的用户Id*/
    private String userId;
    /**发送的消息*/
    private String message;
    /**发送消息的时间*/
    private Date date;


    public PrivateMessage(){}
    public PrivateMessage(String userId, String message) {
        this.userId = userId;
        this.message = message;
        this.date = new Date();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
