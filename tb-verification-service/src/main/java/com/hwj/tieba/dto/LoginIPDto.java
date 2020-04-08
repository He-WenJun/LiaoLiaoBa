package com.hwj.tieba.dto;

import java.util.Date;

public class LoginIPDto {
    /**用户ID*/
    private String userID;
    /**登录ip*/
    private String loginIpAddress;
    /**登录时间*/
    private Date loginDate;
    /**sessionId*/
    private String sessionId;

    public LoginIPDto(){}
    public LoginIPDto(String userID, String loginIpAddress, Date loginDate,String sessionId){
        this.userID = userID;
        this.loginIpAddress = loginIpAddress;
        this.loginDate = loginDate;
        this.sessionId = sessionId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        userID = userID;
    }

    public String getLoginIpAddress() {
        return loginIpAddress;
    }

    public void setLoginIpAddress(String loginIpAddress) {
        this.loginIpAddress = loginIpAddress;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
