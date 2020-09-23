package com.hwj.tieba.dao;
import com.hwj.tieba.entity.ModuleBlackUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ModuleBlackUserMapper {

    List<ModuleBlackUser>queryUserIdAndEnrollDateByModuleId(ModuleBlackUser moduleBlackUser);

    int deleteModuleBlackUser(ModuleBlackUser moduleBlackUser);

    int insertModuleBlackUser(ModuleBlackUser ModuleBlackUser);

}
