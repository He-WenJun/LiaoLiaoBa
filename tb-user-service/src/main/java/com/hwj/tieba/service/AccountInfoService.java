package com.hwj.tieba.service;

import com.hwj.tieba.entity.Account;
import com.hwj.tieba.resp.ServerResponse;

public interface AccountInfoService {
    /**
     * 获取当前登录的用户信息
     * @param sessionId 会话Id
     * @return
     */
    public ServerResponse getUserInfo(String sessionId);


}
