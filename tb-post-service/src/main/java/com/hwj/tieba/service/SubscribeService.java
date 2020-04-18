package com.hwj.tieba.service;

import com.hwj.tieba.entity.Ba;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.BaVO;

import java.util.List;

public interface SubscribeService {
    /**
     * 获取当前用户订阅的吧
     * @param sessionId 当前会话sessionId
     * @return 订阅的吧
     */
    ServerResponse<List<BaVO>> getNowUserSubscribeBa(String sessionId);
}
