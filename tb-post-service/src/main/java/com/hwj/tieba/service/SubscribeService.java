package com.hwj.tieba.service;

import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.ModuleVo;

import java.util.List;

public interface SubscribeService {
    /**
     * 获取当前用户订阅的模块
     * @param sessionId 当前会话sessionId
     * @return 订阅的模块
     */
    ServerResponse<List<ModuleVo>> getSubscribeModule(String sessionId);

    /**
     * 取消当前用户订阅的目标，目标可以是用户，也可以是贴模块
     * @param sessionId 当前会话Id
     * @param objectId 要取消订阅的目标Id
     * @return 取消结果
     */
    ServerResponse<String> delSubscribe(String sessionId,String objectId);

    /**
     * 添加订阅类型为模块的订阅记录
     * @param sessionId 当前会话Id
     * @param moduleName 贴模块名称
     * @return 添加结果
     */
    ServerResponse<String> addSubscribeModule(String sessionId,String moduleName);
}
