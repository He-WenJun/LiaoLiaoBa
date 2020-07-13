package com.hwj.tieba.entity;

import java.util.Date;

public class PostFileRelation {
    private String id;
    private String objectId;
    private String fileId;
    private Date enrollDate;
    private Date updateDate;

    public PostFileRelation(){}
    public PostFileRelation(String id, String objectId, String fileId, Date enrollDate, Date updateDate) {
        this.id = id;
        this.objectId = objectId;
        this.fileId = fileId;
        this.enrollDate = enrollDate;
        this.updateDate = updateDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
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
