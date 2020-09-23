package com.hwj.tieba.service;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Post;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.MessageVo;
import com.hwj.tieba.vo.PostItemVo;
import javafx.geometry.Pos;

import java.util.List;


public interface PostService {
    /**
     * 获取指定模块下的帖子
     * @param post  包含帖子信息
     * @param pageNumber  页码
     * @return
     */
    ServerResponse<PageInfo<PostItemVo>> getPostList(Post post, String pageNumber);


    /**
     * 发表帖子
     * @param post 帖子实例
     * @return
     */
    ServerResponse<String> commitPost(Post post, String sessionId);

    /**
     * 获取指定帖子信息
     * @param postId 帖子Id
     * @return
     */
    ServerResponse<PostItemVo> getPost(String postId);

    /**
     * 获取当前登录账号发布的帖子名称
     * @param sessionId
     * @return
     */
    ServerResponse<List<Post>> getMyPostName(String sessionId);

    /**
     * 修改帖子
     * @param post
     * @return
     */
    ServerResponse<String> updatePost(Post post, String sessionId);

    /**
     * 获取指定用户的帖子列表
     * @param account
     * @param pageNumber
     * @return
     */
    ServerResponse<PageInfo> getPostListByUserId(Account account, int pageNumber);

    /**
     * 模块管理者删除帖子
     * @param post
     * @param deleteReason 删除理由
     * @return
     */
    ServerResponse<String> moduleAdminDelPost(Post post, String deleteReason);

    /**获取当前登录账号的通知信息
     * @param sessionId
     * @return
     */
    ServerResponse<List<MessageVo>> getMessage(String sessionId);

    /**
     * 删除当前登录账号的通知信息
     * @param sessionId
     * @param messageIndex
     * @return
     */
    ServerResponse<String> delMessage(String sessionId, Long messageIndex);

    ServerResponse<PageInfo> getNewestPost(int pageNumber);
}
