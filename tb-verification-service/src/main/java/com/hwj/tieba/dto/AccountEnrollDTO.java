package com.hwj.tieba.dto;

import com.hwj.tieba.entity.Account;

public class AccountEnrollDTO {
    /**注册token*/
    private String token;
    /**要注册的账户信息*/
    private Account account;

    public AccountEnrollDTO(String token, Account account) {
        this.token = token;
        this.account = account;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
