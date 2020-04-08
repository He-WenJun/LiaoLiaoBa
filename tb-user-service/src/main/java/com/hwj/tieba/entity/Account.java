package com.hwj.tieba.entity;

import java.util.Date;

public class Account {
    /**用ID*/
    private String userID;
    /**账号角色ID*/
    private String roleID;
    /**用户名*/
    private String userName;
    /**用户邮箱*/
    private String email;
    /**用户手机号*/
    private String phone;
    /**账户密码*/
    private String password;
    /**注册时间*/
    private Date enrollDate;
    /**修改时间*/
    private Date updateDate;

    @Override
    public String toString() {
        return "Account{" +
                "userID='" + userID + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", enrollDate='" + enrollDate + '\'' +
                '}';
    }

    public String getUserID() {
        return userID;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
