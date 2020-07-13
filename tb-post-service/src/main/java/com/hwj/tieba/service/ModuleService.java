package com.hwj.tieba.service;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.ModuleVo;


public interface ModuleService {
    /**
     * 签到所有订阅的模块
     * @param sessionId 会话Id
     * @return 签到结果
     */
    ServerResponse<String> moduleSignIn(String sessionId);

    /**
     * 签到单个模块
     * @param sessionId
     * @param moduleId 要签到的模块Id
     * @return
     */
    ServerResponse<String> singleModuleSignIn(String sessionId, String moduleId);


    /**
     * 验证模块的签到状态
     * @param sessionId
     * @param moduleId 要验证的模块Id
     * @return
     */
    ServerResponse<String> verificationSignIn(String sessionId, String moduleId);


    /**
     * 获取模块列表
     * @param pageNumber 当前页码
     * @param typeId 模块类型Id
     * @return 模块列表
     */
    ServerResponse<PageInfo<ModuleVo>> moduleList(int pageNumber , String typeId);

    /**
     * 获取模块详细信息
     * @param id 模块Id
     * @return
     */
    ServerResponse<ModuleVo> moduleInfo(String id);
}
