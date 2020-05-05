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

    /**
     * 增加指定账号经验值
     * @param increaseExp 要增加的经验值
     * @param token 增加经验值的令牌
     * @param userId 用户Id
     * @return 增加结果
     */
    ServerResponse<String> increaseAccountExp(Integer increaseExp,String token, String userId);
}
