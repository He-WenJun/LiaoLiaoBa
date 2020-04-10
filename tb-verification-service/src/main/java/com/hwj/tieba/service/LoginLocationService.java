package com.hwj.tieba.service;

import com.hwj.tieba.dto.LoginIPDto;
import com.hwj.tieba.entity.LoginLocation;

public interface LoginLocationService {
    /**
     * 对用户的登录ip进行验证，如若发现异地则对账号绑定的邮箱发送邮件提醒
     * @param loginLocation 包含登录信息
     * @param sessionId 当前用户sessionId
     */
    void loginIPVerification (LoginLocation loginLocation,String sessionId);

    /**
     * 插入登录记录
     * @param loginIP 登录实例
     * @return 返回受影响的行数
     */
    int insertLoginIp(LoginLocation loginIP);
}
