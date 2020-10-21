package com.hwj.tieba.entity;

import java.util.Date;
import java.util.Objects;

public class LoginLocation {
    /**登录记录的ID*/
    private String loginIPID;
    /**用户ID*/
    private String userID;
    /**登录IP*/
    private String iPAddress;
    /**登录的省*/
    private String province;
    /**登录的市*/
    private String city;
    /**登记时间*/
    private Date enrollDate;
    /**修改时间*/
    private Date updateDate;

    public LoginLocation(String loginIPID,String userID, String iPAddress, String province, String city, Date enrollDate,Date updateDate) {
        this.loginIPID = loginIPID;
        this.userID = userID;
        this.iPAddress = iPAddress;
        this.province = province;
        this.city = city;
        this.enrollDate = enrollDate;
        this.updateDate = updateDate;
    }

    public LoginLocation() {}

    @Override
    public int hashCode(){
        return Objects.hash(province,city);
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginLocation loginLocation = (LoginLocation) o;
        //当两个LoginLocation对象的省 市相同时，算作同一个对象
        if(province.equals(((LoginLocation) o).getProvince()) && city.equals(((LoginLocation) o).getCity())){
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "LoginLocation{" +
                "loginIPID='" + loginIPID + '\'' +
                ", userID='" + userID + '\'' +
                ", iPAddress='" + iPAddress + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", enrollDate='" + enrollDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                '}';
    }

    public String getLoginIPID() {
        return loginIPID;
    }

    public void setLoginIPID(String loginIPID) {
        this.loginIPID = loginIPID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getiPAddress() {
        return iPAddress;
    }

    public void setiPAddress(String iPAddress) {
        this.iPAddress = iPAddress;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
