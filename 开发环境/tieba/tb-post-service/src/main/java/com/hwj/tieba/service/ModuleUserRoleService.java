package com.hwj.tieba.service;

import com.hwj.tieba.entity.Module;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.ModuleVo;

import java.util.List;

public interface ModuleUserRoleService {

    /**
     * 获取当前账号管理的模块
     * @param sessionId
     * @return
     */
    ServerResponse<List<ModuleVo>> getMyManagementModule(String sessionId);
}
