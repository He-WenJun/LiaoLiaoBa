package com.hwj.tieba.vo;

import com.hwj.tieba.common.Constants;

import java.util.Date;

public class ModuleVo {
    /**模块Id*/
    private String moduleId;
    /**模块类型Id*/
    private String typeId;
    /**模块类型名称*/
    private String typeName;
    /**模块名称*/
    private String moduleName;
    /**介绍*/
    private String introduce;
    /**模块等级*/
    private Integer level;
    /**模块经验值*/
    private Long exp;
    /**每级所需经验*/
    private Integer levelExp = Constants.LEVEL;
    /**头像的图片Id*/
    private String headPictureId;
    private String headPictureSrc;
    /**背景图片Id*/
    private String moduleBackgroundPictureId;
    private String moduleBackgroundPictureSrc;
    /**模块的订阅数量*/
    private String countSubscribe;
    /**模块的帖子数量*/
    private String countPost;
    /**创建时间*/
    private Date enrollDate;

    public ModuleVo(){}

    public ModuleVo(String moduleId, String typeId, String moduleName, String introduce, Integer level, Long exp, String headPictureSrc, String moduleBackgroundPictureSrc, Date enrollDate) {
        this.moduleId = moduleId;
        this.typeId = typeId;
        this.moduleName = moduleName;
        this.introduce = introduce.length() >= 17 ? introduce.substring(0,13) + "..." : introduce;
        this.introduce = introduce;
        this.level = level;
        this.exp = exp;
        this.headPictureSrc = headPictureSrc.substring(headPictureSrc.lastIndexOf("file")-1,headPictureSrc.length());
        if(moduleBackgroundPictureSrc != null){
            this.moduleBackgroundPictureSrc = moduleBackgroundPictureSrc.substring(moduleBackgroundPictureSrc.lastIndexOf("file")-1,moduleBackgroundPictureSrc.length());
        }
        this.enrollDate = enrollDate;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduceOmit(String introduce) {
        this.introduce = introduce.length() >= 17 ? introduce.substring(0,13) + "..." : introduce;
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

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public Integer getLevelExp() {
        return levelExp;
    }

    public void setLevelExp(Integer levelExp) {
        this.levelExp = levelExp;
    }

    public String getHeadPictureSrc() {
        return headPictureSrc;
    }

    public void setHeadPictureSrc(String headPictureSrc) {
        this.headPictureSrc = headPictureSrc;
    }

    public String getModuleBackgroundPictureSrc() {
        return moduleBackgroundPictureSrc;
    }

    public void setModuleBackgroundPictureSrc(String moduleBackgroundPictureSrc) {
        this.moduleBackgroundPictureSrc = moduleBackgroundPictureSrc;
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

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getHeadPictureId() {
        return headPictureId;
    }

    public void setHeadPictureId(String headPictureId) {
        this.headPictureId = headPictureId;
    }

    public String getModuleBackgroundPictureId() {
        return moduleBackgroundPictureId;
    }

    public void setModuleBackgroundPictureId(String moduleBackgroundPictureId) {
        this.moduleBackgroundPictureId = moduleBackgroundPictureId;
    }
}
