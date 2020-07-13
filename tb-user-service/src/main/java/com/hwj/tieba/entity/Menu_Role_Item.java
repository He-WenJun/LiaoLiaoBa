package com.hwj.tieba.entity;

import java.util.Date;

public class Menu_Role_Item {
    private Integer id;
    private Integer menuParentId;
    private Integer roleId;
    private Date enrollDate;
    private Date updateDate;

    public Menu_Role_Item(){}
    public Menu_Role_Item(Integer id, Integer menuParentId, Integer roleId, Date enrollDate, Date updateDate) {
        this.id = id;
        this.menuParentId = menuParentId;
        this.roleId = roleId;
        this.enrollDate = enrollDate;
        this.updateDate = updateDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMenuParentId() {
        return menuParentId;
    }

    public void setMenuParentId(Integer menuParentId) {
        this.menuParentId = menuParentId;
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
