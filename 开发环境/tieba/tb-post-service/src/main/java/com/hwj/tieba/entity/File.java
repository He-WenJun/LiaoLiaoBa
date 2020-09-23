package com.hwj.tieba.entity;

import java.util.Date;
import java.util.Map;

public class File {
    /**文件ID*/
    private String id;
    /**文件名称*/
    private String name;
    /**文件路径*/
    private String src;
    /**文件类型Id*/
    private Integer typeId;
    /**文件后缀*/
    private String suffix;
    /**文件插入时间*/
    private Date enrollDate;
    /**文件修改时间*/
    private Date updateDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
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
