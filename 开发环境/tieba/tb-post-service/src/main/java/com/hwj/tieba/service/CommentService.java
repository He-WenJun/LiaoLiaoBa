package com.hwj.tieba.service;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Comment;
import com.hwj.tieba.entity.Reply;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.CommentItemVo;

import java.util.List;

public interface CommentService {
    /**
     * 获取评论
     * @param postId 帖子Id
     * @param pageNumber 页码
     * @return
     */
    ServerResponse<PageInfo<CommentItemVo>> getComment(String postId, int pageNumber);

    /**
     * 插入评论
     * @param comment 评论实例
     * @param uploadIdList 评论上传的图片Id
     * @param userId
     * @param sessionId
     * @return
     */
    ServerResponse<String> insertComment(Comment comment, String userId, List<String> uploadIdList, String sessionId);

    /**
     * 插入一条回复信息
     * @param reply 回复实例
     * @param targetUserId 回复用户的用户Id
     * @param sessionId
     * @return
     */
    ServerResponse insertReply(Reply reply ,String targetUserId ,String sessionId);
}
