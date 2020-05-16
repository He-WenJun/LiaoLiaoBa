package com.hwj.tieba.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface CommentMapper {
    /**
     * 获取帖子中最新一条评论的回复时间
     * @param postIdList 帖子Id集合
     * @return
     */
    List<Map<String,Object>> latestCommentDate (@Param("postIdList") List<String> postIdList);

    /**
     *  获取帖子中的评论数量
     * @param postIdList 帖子Id集合
     * @return
     */
    List<Map<String,Object>> commentCount(@Param("postIdList") List<String> postIdList);
}
