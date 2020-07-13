package com.hwj.tieba.entity;

import java.util.Date;

public class MenuParent {
    /**类型Id*/
    private Integer id;
    /**类型名称*/
    private String name;
    /**图标样式Id*/
    private String icon;
    /**资源位置*/
    private String resource;
    /**插入时间*/
    private Date enrollDate;
    /**修改时间*/
    private Date update;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }
}
