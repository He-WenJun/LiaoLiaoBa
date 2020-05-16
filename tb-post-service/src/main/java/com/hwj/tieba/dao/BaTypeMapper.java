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

    /**
     * 按子类型Id查询对应的父类型Id
     * @param sonTypeId 子类型Id
     * @return
     */
    Integer queryParentIdBySonTypeId(@Param("sonTypeId") String sonTypeId);

    /**
     * 根据id查询父类型
     * @param parentId 父类型Id
     * @return
     */
    BaParentType queryParentTypeById(@Param("id") int parentId);

    /**
     * 根据Id查询子类型
     * @param sonId 子类型Id
     * @return
     */
    BaSonType querySonTypeById(@Param("id") int sonId);
}
