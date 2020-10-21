package com.hwj.tieba.service;

import com.hwj.tieba.entity.Account;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.AccountVo;
import com.hwj.tieba.vo.PrivateMessageVo;

import java.util.List;

public interface PrivateMessageService {
    /**
     * 添加用户到私信列表
     * @param targetUserId 要添加的账号
     * @param sessionId
     * @return
     */
    ServerResponse<String> addUserGoToPrivateMessageList(String targetUserId, String sessionId);

    /**
     * 获取当前登录账号的私信列表
     * @param sessionId
     * @return
     */
    ServerResponse<List<AccountVo>> getPrivateMessageList(String sessionId);

    /**
     * 获取私信列表的历史消息
     * @param targetAccount 聊天对象的账号
     * @param startIndex 历史记录的起始下标
     * @param sessionId
     * @return
     */
    ServerResponse<PrivateMessageVo> getOldMessage(Account targetAccount, long startIndex, String sessionId);

    /**
     * 验证消息接收方是否拒收了消息发送方
     * @param targetUserId 接收方的用户Id
     * @param userId 发送方用户Id
     * @return
     */
    ServerResponse<String> verifyMessage(String targetUserId, String userId);

    /**
     * 删除私信列表中的用户
     * @param targetUserId 要删用户的Id
     * @param sessionId
     * @return
     */
    ServerResponse<String> delPrivateMessage(String targetUserId, String sessionId);

    /**
     * 获取最后一次私信的聊天用户Id
     * @param sessionId
     * @return
     */
    ServerResponse<String> lastMessage(String sessionId);
}
