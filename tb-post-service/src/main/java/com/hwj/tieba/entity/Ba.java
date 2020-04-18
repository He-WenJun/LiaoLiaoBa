package com.hwj.tieba.entity;

import java.util.Date;

public class Ba {
    /**吧Id*/
    private String baId;
    /**吧类型*/
    private Integer typeId;
    /**吧名称*/
    private String baName;
    /**介绍*/
    private String introduce;
    /**经验值*/
    private Long exp;
    /**头像的图片Id*/
    private String headPictureId;
    /**背景图片Id*/
    private String backgroundPictureId;
    /**创建时间*/
    private Date enrollDate;
    /**修改时间*/
    private Date updateDate;

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
    public Integer getTypeId() {
       return this.typeId;
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

    public String getBaId() {
        return baId;
    }

    public void setBaId(String baId) {
        this.baId = baId;
    }

    public String getBaName() {
        return baName;
    }

    public void setBaName(String baName) {
        this.baName = baName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
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
