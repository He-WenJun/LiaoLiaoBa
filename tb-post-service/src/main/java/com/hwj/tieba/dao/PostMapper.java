package com.hwj.tieba.dao;

import com.hwj.tieba.entity.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface PostMapper {
    /**
     * 查询对应模块下的帖子数量
     * @param moduleIdList  模块id列表
     * @return 帖子数量列表
     */
    List<Map<String,Object>> countPost(@Param("moduleIdList") List<String> moduleIdList);

    /**
     * 查询指定模块下的帖子,并按插入时间降序排序
     * @param post
     * @return
     */
    List<Post> queryPostByModuleIdAndPostName(Post post);

    /**
     * 插入一条帖子
     * @param post 帖子实例
     * @return
     */
    int insertPost(@Param("post") Post post);

    /**
     * 查询一条帖子
     * @param postId 帖子Id
     * @return
     */
    Post queryPostByPostId(@Param("postId") String postId);

    /**
     * 根据用户Id查询帖子名字
     * @param userId 用户Id
     * @return
     */
    List<Post> queryPostNameByUserId(@Param("userId") String userId);

    /**
     * 根据帖子Id修改帖子
     * @param post
     * @return
     */
    int updatePostByPostId(@Param("post") Post post);

    int deletePostByPostId(@Param("post") Post post);

    int updateStateIdByPostId(@Param("post") Post post,@Param("stateId") int StateId);

    int updateReadContentByPostId(@Param("postId") String postId);
}
