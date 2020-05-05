package com.hwj.tieba.vo;

import com.hwj.tieba.common.Constants;

import java.util.Date;

public class AccountVO {
    private String userId;
    /**用户名*/
    private String userName;
    /**用户邮箱*/
    private String email;
    /**用户手机号*/
    private String phone;
    /**注册时间*/
    private Date enrollDate;
    /**头像路径*/
    private String headPictureSrc;
    /**账号经验值*/
    private Long exp;
    /**账号级别所*/
    private Integer level;
    /**每级所需经验*/
    private Integer levelExp = Constants.LEVEL;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getLevelExp() {
        return levelExp;
    }

    public void setLevelExp(Integer levelExp) {
        this.levelExp = levelExp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getHeadPictureSrc() {
        return headPictureSrc;
    }

    public void setHeadPictureSrc(String headPictureSrc) {
        this.headPictureSrc = headPictureSrc;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }
}
