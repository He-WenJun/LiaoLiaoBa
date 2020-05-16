package com.hwj.tieba.vo;

import com.hwj.tieba.common.Constants;

import java.util.Date;

public class BaVO {
    /**吧Id*/
    private String baId;
    /**吧类型Id*/
    private String typeId;
    /**吧类型名称*/
    private String typeName;
    /**吧名称*/
    private String baName;
    /**介绍*/
    private String introduce;
    /**吧等级*/
    private Integer level;
    /**吧经验值*/
    private Long exp;
    /**每级所需经验*/
    private Integer levelExp = Constants.LEVEL;
    /**头像的图片Id*/
    private String headPictureSrc;
    /**背景图片Id*/
    private String backgroundPictureSrc;
    /**贴吧的订阅数量*/
    private String countSubscribe;
    /**贴吧的帖子数量*/
    private String countPost;
    /**创建时间*/
    private Date enrollDate;

    public BaVO(){}

    public BaVO(String baId, String typeId, String baName, String introduce, Integer level, Long exp, String headPictureSrc, String backgroundPictureSrc, Date enrollDate) {
        this.baId = baId;
        this.typeId = typeId;
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
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
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

    public String getCountSubscribe() {
        return countSubscribe;
    }

    public void setCountSubscribe(String countSubscribe) {
        this.countSubscribe = countSubscribe;
    }

    public String getCountPost() {
        return countPost;
    }

    public void setCountPost(String countPost) {
        this.countPost = countPost;
    }

    public Integer getLevelExp() {
        return levelExp;
    }

    public void setLevelExp(Integer levelExp) {
        this.levelExp = levelExp;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
