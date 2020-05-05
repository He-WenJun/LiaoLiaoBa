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
     * 根据名称查询贴吧
     * @param baName 贴吧名称
     * @return 吧
     */
    Ba queryBaById(@Param("baName") String baName);

    /**
     * 根据吧id集合查询多个贴吧
     * @param idList 吧id集合
     * @return 吧列表
     */
    List<Ba> queryBaByIdList(@Param("idList") List<String> idList);

    /**
     * 根据吧Id修改对应经验值
     * @param  baIdList 吧id列表
     * @param  increaseExp 要增加的经验值
     * @return 受影响行数
     */
    Integer updateBaExpByBaId(@Param("baIdList") List<String> baIdList,@Param("increaseExp") int increaseExp);
}
