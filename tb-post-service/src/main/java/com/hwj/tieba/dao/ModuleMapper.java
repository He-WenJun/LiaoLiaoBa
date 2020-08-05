package com.hwj.tieba.dao;

import com.hwj.tieba.entity.Module;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ModuleMapper {
    /**
     * 根据名称查询模块
     * @param moduleName 模块名称
     * @return 模块
     */
    Module queryModuleByModuleName(@Param("moduleName") String moduleName);

    /**
     * 根据模块id集合查询多个模块
     * @param idList 模块id集合
     * @return 模块列表
     */
    List<Module> queryModuleByIdList(@Param("idList") List<String> idList);

    /**
     * 根据模块id查询模块
     * @param id 模块id集合
     * @return 模块列表
     */
    Module queryModuleByModuleId(@Param("id") String id);

    /**
     * 根据类型Id查询对应的模块
     * @param typeId 模块类型Id
     * @return 模块列表
     */
    List<Module> queryModuleByTypeId(@Param("typeId") String typeId);

    /**
     * 根据模块Id修改对应经验值
     * @param  moduleIdList 模块id列表
     * @param  increaseExp 要增加的经验值
     * @return 受影响行数
     */
    Integer updateModuleExpByModuleId(@Param("moduleIdList") List<String> moduleIdList,@Param("increaseExp") int increaseExp);

    Integer updateModule(Module module);

    Integer insertModule(Module module);
}
