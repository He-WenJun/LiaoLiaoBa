package com.hwj.tieba.entity;

import java.util.Date;

public class Punishment {
    /**惩罚记录的ID*/
    private String punishmentID;
    /**用户ID*/
    private String userID;
    /**生效时间*/
    private Date takeEffectDate;
    /**结束时间*/
    private Date endDate;
    /**状态ID*/
    private Integer stateID;
    /**惩罚原因*/
    private String reason;

    @Override
    public String toString() {
        return "Punishment{" +
                "punishmentID='" + punishmentID + '\'' +
                ", userID='" + userID + '\'' +
                ", takeEffectDate=" + takeEffectDate +
                ", endDate=" + endDate +
                ", stateID=" + stateID +
                ", reason='" + reason + '\'' +
                '}';
    }

    public String getPunishmentID() {
        return punishmentID;
    }

    public void setPunishmentID(String punishmentID) {
        this.punishmentID = punishmentID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Date getTakeEffectDate() {
        return takeEffectDate;
    }

    public void setTakeEffectDate(Date takeEffectDate) {
        this.takeEffectDate = takeEffectDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getStateID() {
        return stateID;
    }

    public void setStateID(Integer stateID) {
        this.stateID = stateID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
