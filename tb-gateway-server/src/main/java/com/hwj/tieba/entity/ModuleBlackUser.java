package com.hwj.tieba.entity;

import java.util.Date;

public class ModuleBlackUser {
    /**黑名单Id*/
    private String id;
    /**模块Id*/
    private String moduleId;
    /**用户Id*/
    private String userId;
    /**加入黑名单的原因*/
    private String describe;
    /**插入时间*/
    private Date enrollDate;
    /**修改时间*/
    private Date updateDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
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
