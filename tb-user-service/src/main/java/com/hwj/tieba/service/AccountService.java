package com.hwj.tieba.service;

import com.hwj.tieba.entity.Account;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.AccountVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AccountService {
    /**
     * 账号登录
     * @param accountNumber 账号 既可以是用户名也可以是邮箱
     * @param password 账号密码
     * @param request 用于获取请求头中的sessionId,以及登录ip
     * @return  返回一个账号实例
     */
    public ServerResponse<AccountVO> login(String accountNumber, String password, String loinVerifyCode, HttpServletRequest request,HttpServletResponse response);

    /**
     * 登录验证码
     * @param request   用于获取请求头中的sessionId
     * @param response   响应验证码图片response
     */
    public void verificationCode(HttpServletRequest request, HttpServletResponse response);

    /**
     * 注册 将注册信息存入redis
     * @param account 写入redis中的账号
     * @return 返回响应状态
     */
    public ServerResponse enrollHold(Account account);

    /**
     * 注册 将注册信息存入数据库
     * @param userName 用户名
     * @return
     */
    public ServerResponse enroll(String userName);

    /**
     * 验证注册状态
     * @param userName 用户名
     * @return
     */
    public ServerResponse enrollVerification(String userName);
}
