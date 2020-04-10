package com.hwj.tieba.service;

import com.hwj.tieba.entity.Account;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.AccountVO;

public interface AccountInfoService {
    /**
     * 获取当前登录的用户信息
     * @param sessionId 会话Id
     * @return 返回当前登录的账户信息
     */
    ServerResponse<AccountVO> getUserInfo(String sessionId);


}
