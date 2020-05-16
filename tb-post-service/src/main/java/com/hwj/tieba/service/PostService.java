package com.hwj.tieba.service;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.PostItemVO;


public interface PostService {
    /**
     * 获取指定贴吧下的帖子
     * @param baId  贴吧Id
     * @param pageNumber  页码
     * @return
     */
    ServerResponse<PageInfo<PostItemVO>> getPost(String baId, String pageNumber);

}
