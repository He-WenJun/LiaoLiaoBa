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
    ServerResponse<List<BaVO>> getSubscribeBa(String sessionId);

    /**
     * 取消当前用户订阅的目标，目标可以是用户，也可以是贴吧
     * @param sessionId 当前会话Id
     * @param objectId 要取消订阅的目标Id
     * @return 取消结果
     */
    ServerResponse<String> delSubscribe(String sessionId,String objectId);

    /**
     * 添加订阅类型为吧的订阅记录
     * @param sessionId 当前会话Id
     * @param baName 贴吧名称
     * @return 添加结果
     */
    ServerResponse<String> addSubscribeBa(String sessionId,String baName);
}
