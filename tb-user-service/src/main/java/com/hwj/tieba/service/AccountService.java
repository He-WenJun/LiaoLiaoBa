package com.hwj.tieba.service;

import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.AccountInfo;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.AccountVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AccountService {
    /**
     * 账号登录
     * @param accountNumber 账号 既可以是用户名也可以是邮箱
     * @param password 密码
     * @param loinVerifyCode 验证码
     * @param request 用于获取请求头中的sessionId,以及登录ip
     * @param response 用于写入cookie
     * @return  返回登录结果
     */
    ServerResponse<String> login(String accountNumber, String password, String loinVerifyCode, HttpServletRequest request,HttpServletResponse response);

    /**
     * 登录验证码
     * @param request   用于获取请求头中的sessionId
     * @param response   响应验证码图片response
     */
    void verificationCode(HttpServletRequest request, HttpServletResponse response);

    /**
     * 注册 将注册信息存入redis
     * @param account 写入redis中的账号
     * @param accountInfo 写入redis中的账号详细信息
     * @return 返回响应状态
     */
    ServerResponse<String> enrollHold(Account account, AccountInfo accountInfo);

    /**
     * 注册 将注册信息存入数据库
     * @param token 注册令牌
     * @return 返回注册结果
     */
    ServerResponse<String> enroll(String token);

    /**
     * 验证注册状态
     * @param userId 用户Id
     * @param sessionId 当前sessionId
     * @return 验证结果
     */
    ServerResponse<String> enrollVerification(String userId,String sessionId);

    /**
     * 支付宝登录
     * @param request
     * @param response
     * @return 登录结果
     */
    ServerResponse zhiFuBaoLogin(HttpServletRequest request,HttpServletResponse response);


}
