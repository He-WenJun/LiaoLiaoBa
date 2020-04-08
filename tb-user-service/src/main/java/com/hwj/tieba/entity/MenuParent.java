package com.hwj.tieba.entity;

import java.util.Date;

public class MenuParent {
    /**类型Id*/
    private Integer id;
    /**类型名称*/
    private String name;
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
