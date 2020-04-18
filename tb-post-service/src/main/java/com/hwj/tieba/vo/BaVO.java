package com.hwj.tieba.vo;

import java.util.Date;

public class BaVO {
    /**吧Id*/
    private String baId;
    /**吧类型*/
    private String type;
    /**吧名称*/
    private String baName;
    /**介绍*/
    private String introduce;
    /**吧等级*/
    private Integer level;
    /**吧经验值*/
    private Long exp;
    /**头像的图片Id*/
    private String headPictureSrc;
    /**背景图片Id*/
    private String backgroundPictureSrc;
    /**创建时间*/
    private Date enrollDate;

    public BaVO(String baId, String type, String baName, String introduce, Integer level, Long exp, String headPictureSrc, String backgroundPictureSrc, Date enrollDate) {
        this.baId = baId;
        this.type = type;
        this.baName = baName;
        this.introduce = introduce;
        this.level = level;
        this.exp = exp;
        this.headPictureSrc = headPictureSrc;
        this.backgroundPictureSrc = backgroundPictureSrc;
        this.enrollDate = enrollDate;
    }

    public String getHeadPictureSrc() {
        return headPictureSrc;
    }

    public void setHeadPictureSrc(String headPictureSrc) {
        this.headPictureSrc = headPictureSrc;
    }

    public String getBackgroundPictureSrc() {
        return backgroundPictureSrc;
    }

    public void setBackgroundPictureSrc(String backgroundPictureSrc) {
        this.backgroundPictureSrc = backgroundPictureSrc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public String getBaId() {
        return baId;
    }

    public void setBaId(String baId) {
        this.baId = baId;
    }

    public String getTypeId() {
        return type;
    }

    public void setTypeId(String type) {
        this.type = type;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }
}
