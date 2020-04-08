package com.hwj.tieba.entity;

import java.util.Date;

public class Binding {
    /**绑定记录的Id*/
    private String bindingId;
    /**用户Id*/
    private String userId;
    /**绑定类型*/
    private Integer bindingType;
    /**绑定的账号*/
    private String account;
    /**绑定时间*/
    private Date enrollDate;
    /**修改时间*/
    private Date updateDate;

    public String getBindingId() {
        return bindingId;
    }

    public void setBindingId(String bindingId) {
        this.bindingId = bindingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getBindingType() {
        return bindingType;
    }

    public void setBindingType(Integer bindingType) {
        this.bindingType = bindingType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
