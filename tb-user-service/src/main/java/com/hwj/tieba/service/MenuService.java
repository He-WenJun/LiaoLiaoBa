package com.hwj.tieba.service;

import com.hwj.tieba.resp.ServerResponse;

public interface MenuService {
    /**
     * 根据当前用户登录的角色获取其对于的菜单
     * @param sessionId 会话Id
     * @return 包含菜单信息
     */
    public ServerResponse getMenu(String sessionId);
}
