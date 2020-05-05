package com.hwj.tieba.service;

import com.hwj.tieba.resp.ServerResponse;

public interface BaService {
    /**
     * 签到所有订阅的贴吧
     * @param sessionId 会话Id
     * @return 签到结果
     */
    ServerResponse<String> baSignIn(String sessionId);
}
