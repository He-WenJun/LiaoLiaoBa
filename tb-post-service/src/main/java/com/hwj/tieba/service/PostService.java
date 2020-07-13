package com.hwj.tieba.service;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.entity.Post;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.PostItemVo;

import java.util.List;


public interface PostService {
    /**
     * 获取指定模块下的帖子
     * @param moduleId  模块Id
     * @param pageNumber  页码
     * @return
     */
    ServerResponse<PageInfo<PostItemVo>> getPostList(String moduleId, String pageNumber);


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
}
