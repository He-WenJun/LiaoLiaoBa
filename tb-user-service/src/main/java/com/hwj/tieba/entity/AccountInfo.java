package com.hwj.tieba.entity;

import java.util.Date;

public class AccountInfo {
    /**用户ID*/
    private String userId;
    /**用户年龄*/
    private Integer age;
    /**签名档*/
    private String introduce;
    /**头像图片ID*/
    private String headPictureId;
    /**北京图片ID*/
    private String backgroundPictureId;
    /**账号经验值*/
    private Long exp;
    /**插入时间*/
    private Date enrollDate;
    /**修改时间*/
    private Date updateDate;

    public AccountInfo(){}
    public AccountInfo(String userId, Integer age, String introduce, String headPictureId, String backgroundPictureId, Long exp, Date enrollDate, Date updateDate) {
        this.userId = userId;
        this.age = age;
        this.introduce = introduce;
        this.headPictureId = headPictureId;
        this.backgroundPictureId = backgroundPictureId;
        this.exp = exp;
        this.enrollDate = enrollDate;
        updateDate = updateDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getHeadPictureId() {
        return headPictureId;
    }

    public void setHeadPictureId(String headPictureId) {
        this.headPictureId = headPictureId;
    }

    public String getBackgroundPictureId() {
        return backgroundPictureId;
    }

    public void setBackgroundPictureId(String backgroundPictureId) {
        this.backgroundPictureId = backgroundPictureId;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
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
