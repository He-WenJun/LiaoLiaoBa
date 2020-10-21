package com.hwj.tieba.dao;

import com.hwj.tieba.entity.Comment;
import com.hwj.tieba.entity.Reply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ReplyMapper {
    /**
     * 查询回复
     * @param commentList 评论集合
     * @return
     */
    List<Reply> queryReplyListByCommentId(@Param("commentList") List<Comment> commentList);

    /**
     * 插入一条回复
     * @param reply 回复实例
     * @return
     */
    int insertReply(@Param("reply") Reply reply);
}
