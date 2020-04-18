package com.hwj.tieba.dao;

import com.hwj.tieba.entity.Ba;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BaMapper {
    /**
     * 根据id查询吧
     * @param id 吧id
     * @return 吧
     */
    Ba queryBaById(@Param("id") String id);

    /**
     * 根据吧id集合查询多个吧
     * @param idList 吧id集合
     * @return 吧列表
     */
    List<Ba> queryBaByIdList(@Param("idList") List<String> idList);
}
