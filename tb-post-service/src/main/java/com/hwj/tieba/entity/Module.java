package com.hwj.tieba.entity;

import java.util.Date;

public class Module {
    /**模块Id*/
    private String moduleId;
    /**模块类型*/
    private Integer typeId;
    /**模块名称*/
    private String moduleName;
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

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
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

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
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
