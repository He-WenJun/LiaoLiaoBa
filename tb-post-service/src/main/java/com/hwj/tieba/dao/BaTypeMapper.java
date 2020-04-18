package com.hwj.tieba.dao;

import com.hwj.tieba.entity.BaParentType;
import com.hwj.tieba.entity.BaSonType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BaTypeMapper {
    /**
     * 查询吧的父类型
     * @return 父类型集合
     */
    List<BaParentType> queryBaParentType ();

    /**
     *根据父类型Id查询对应的子类型
     * @param idList 父类型Id集合
     * @return 子类型集合
     */
    List<BaSonType> queryBaSonTypeByParentId (@Param("idList") List<Integer> idList);
}
