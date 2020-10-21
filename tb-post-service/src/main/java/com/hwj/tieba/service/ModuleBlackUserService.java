package com.hwj.tieba.service;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Module;
import com.hwj.tieba.entity.ModuleBlackUser;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.AccountVo;
import com.hwj.tieba.vo.ModuleBlackUserVo;

public interface ModuleBlackUserService {
    /**
     * 查询黑名单用户
     * @param moduleBlackUser 黑名单用户
     * @param searchUserName 需要搜索的用户名
     * @return
     */
    ServerResponse<PageInfo<ModuleBlackUserVo>> queryModuleBlackUserList(ModuleBlackUser moduleBlackUser, int pageNumber, Account searchUserName);

    /**
     * 删除黑名单用户
     * @param moduleBlackUser 黑名单用户
     * @return
     */
    ServerResponse<String> deleteModuleBlackUser(ModuleBlackUser moduleBlackUser);


    /**
     * 添加黑名单用户
     * @param moduleBlackUser 黑名单用户
     * @param searchUserName 需要搜索的用户名
     * @return
     */
    ServerResponse<String> addModuleBlackUser(ModuleBlackUser moduleBlackUser,Account searchUserName);
}
