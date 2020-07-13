package com.hwj.tieba.vo;

import com.hwj.tieba.common.Constants;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.AccountInfo;
import org.springframework.util.StringUtils;

import java.util.Date;

public class AccountVO {
    private String userId;
    /**用户名*/
    private String userName;
    /**用户邮箱*/
    private String email;
    /**用户手机号*/
    private String phone;
    /**注册时间*/
    private Date enrollDate;
    /**头像路径*/
    private String headPictureSrc;
    /**背景图片路径*/
    private String backgroundPicture;
    /**账号经验值*/
    private Long exp;
    /**账号级别所*/
    private Integer level;
    /**角色Id*/
    private String roleId;

    private AccountInfo accountInfo;

    public AccountVO() {}

    public AccountVO(Account account, AccountInfo accountInfo) {
        this.userId = account.getUserId();
        this.userName = account.getUserName();
        //若邮箱不为空则进行过滤
        if(!StringUtils.isEmpty(account.getEmail()) && account.getEmail().indexOf("@") != -1){
            StringBuffer sbEmail = new StringBuffer(account.getEmail());
            int index = sbEmail.lastIndexOf("@");
            sbEmail.replace(index-5,index-1,"****");
            this.email = sbEmail.toString();
        }
        this.enrollDate = account.getEnrollDate();
        this.roleId = account.getRoleId();

        if(accountInfo.getExp() >= Constants.LEVEL){
            this.level = (int) (accountInfo.getExp() / Constants.LEVEL);
            this.exp = accountInfo.getExp() % Constants.LEVEL;
        }else {
            this.level = 1;
            this.exp = accountInfo.getExp();
        }

        this.accountInfo = accountInfo;

    }

    /**每级所需经验*/



    private Integer levelExp = Constants.LEVEL;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getLevelExp() {
        return levelExp;
    }

    public void setLevelExp(Integer levelExp) {
        this.levelExp = levelExp;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getEnrollDate() {
        return enrollDate;
    }

    public void setEnrollDate(Date enrollDate) {
        this.enrollDate = enrollDate;
    }

    public String getHeadPictureSrc() {
        return headPictureSrc;
    }

    public void setHeadPictureSrc(String headPictureSrc) {
        this.headPictureSrc = headPictureSrc;
    }

    public Long getExp() {
        return exp;
    }

    public void setExp(Long exp) {
        this.exp = exp;
    }

    public String getBackgroundPicture() {
        return backgroundPicture;
    }

    public void setBackgroundPicture(String backgroundPicture) {
        this.backgroundPicture = backgroundPicture;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public AccountInfo getAccountInfo() {
        return accountInfo;
    }

    public void setAccountInfo(AccountInfo accountInfo) {
        this.accountInfo = accountInfo;
    }
}
