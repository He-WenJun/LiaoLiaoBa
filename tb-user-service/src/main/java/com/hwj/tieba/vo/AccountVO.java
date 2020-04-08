package com.hwj.tieba.vo;

import java.util.Date;

public class AccountVO {
    /**用户名*/
    private String UserName;
    /**用户邮箱*/
    private String email;
    /**用户手机号*/
    private String phone;
    /**注册时间*/
    private Date enrollDate;

    @Override
    public String toString() {
        return "AccountVO{" +
                "UserName='" + UserName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", enrollDate='" + enrollDate + '\'' +
                '}';
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }
}
