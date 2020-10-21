package com.hwj.tieba.service;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Subscribe;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.AccountVo;
import com.hwj.tieba.vo.ModuleVo;

import java.util.List;

public interface SubscribeService {
    /**
     * 查询当前用户订阅的模块
     * @param sessionId 当前会话sessionId
     * @return 订阅的模块
     */
    ServerResponse<List<ModuleVo>> getSubscribeModule(String sessionId);

    /**
     * 获取指定用户订阅的模块
     * @param account
     * @return
     */
    ServerResponse<List<ModuleVo>> getSubscribeModuleByUserId(Account account);

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

    /**
     * 添加订阅类型为用户的订阅记录
     * @param sessionId
     * @param subscribe
     * @return
     */
    ServerResponse<String> addSubscribeUser(String sessionId, Subscribe subscribe);

    /**
     * 获取指定用户关注的用户列表
     * @param account
     * @return
     */
    ServerResponse<PageInfo<AccountVo>> himConcernList(Account account, int pageNumber);

    /**
     * 获取关注此用户的用户列表
     * @param account
     * @return
     */
    ServerResponse<PageInfo<AccountVo>> concernHimList(Account account, int pageNumber);
}
