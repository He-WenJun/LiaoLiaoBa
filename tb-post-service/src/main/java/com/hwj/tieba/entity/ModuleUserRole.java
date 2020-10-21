package com.hwj.tieba.entity;

import java.util.Date;

public class ModuleUserRole {
    /**id*/
    private String id;
    /**模块Id*/
    private String moduleId;
    /**用户Id*/
    private String userId;
    /**角色Id*/
    private Integer roleId;
    /**插入时间*/
    private Date enrollDate;
    /**修改时间*/
    private Date updateDate;


    public ModuleUserRole(String id, String moduleId, String userId, Integer roleId, Date enrollDate, Date updateDate) {
        this.id = id;
        this.moduleId = moduleId;
        this.userId = userId;
        this.roleId = roleId;
        this.enrollDate = enrollDate;
        this.updateDate = updateDate;
    }

    public ModuleUserRole(){}
    public ModuleUserRole(String userId) {
        this.userId = userId;
    }

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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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
