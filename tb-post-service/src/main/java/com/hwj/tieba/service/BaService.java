package com.hwj.tieba.service;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.BaVO;


public interface BaService {
    /**
     * 签到所有订阅的贴吧
     * @param sessionId 会话Id
     * @return 签到结果
     */
    ServerResponse<String> baSignIn(String sessionId);

    /**
     * 签到单个贴吧
     * @param sessionId
     * @param baId 要签到的贴吧Id
     * @return
     */
    ServerResponse<String> singleBaSignIn(String sessionId, String baId);


    /**
     * 验证贴吧的签到状态
     * @param sessionId
     * @param baId 要验证的贴吧Id
     * @return
     */
    ServerResponse<String> verificationSignIn(String sessionId, String baId);


    /**
     * 获取贴吧列表
     * @param pageNumber 当前页码
     * @param typeId 贴吧类型Id
     * @return 贴吧列表
     */
    ServerResponse<PageInfo<BaVO>> baList(int pageNumber , String typeId);

    /**
     * 获取贴吧详细信息
     * @param id 贴吧Id
     * @return
     */
    ServerResponse<BaVO> baInfo(String id);
}
