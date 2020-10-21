package com.hwj.tieba.dao;

import com.hwj.tieba.entity.ModuleUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ModuleUserRoleMapper {

    ModuleUserRole queryRoleIdByModuleIdAndUserId(ModuleUserRole moduleUserRole);

    List<ModuleUserRole> queryModuleIdByUserId(ModuleUserRole moduleUserRole);

    Integer insertModuleUserRole(ModuleUserRole moduleUserRole);
}
