package com.hwj.tieba.entity;

import java.util.Date;

public class Subscribe {
    /**订阅Id*/
    private String id;
    /**订阅类型Id*/
    private Integer typeId;
    /**被订阅目标的Id*/
    private String objectId;
    /**订阅者Id*/
    private String userId;
    /**订阅时间*/
    private Date enrollDate;
    /**修改时间*/
    private Date updateDate;


    public Subscribe(){}
    public Subscribe(String id, Integer typeId, String objectId, String userId, Date enrollDate, Date updateDate) {
        this.id = id;
        this.typeId = typeId;
        this.objectId = objectId;
        this.userId = userId;
        this.enrollDate = enrollDate;
        this.updateDate = updateDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
