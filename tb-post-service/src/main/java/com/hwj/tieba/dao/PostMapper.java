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
     * 查询对应吧下的帖子数量
     * @param baIdList  吧id列表
     * @return 帖子数量列表
     */
    List<Map<String,Object>> countPost(@Param("baIdList") List<String> baIdList);

    /**
     * 查询指定贴吧下的帖子,并按插入时间降序排序
     * @param BaId 贴吧Id
     * @return
     */
    List<Post> queryPostByBaId(@Param("baId") String BaId);
}
