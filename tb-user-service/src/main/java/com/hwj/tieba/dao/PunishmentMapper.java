package com.hwj.tieba.dao;

import com.hwj.tieba.entity.Punishment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface PunishmentMapper {
    /**
     * 查询指定账号的生效违规记录
     * @param userId 用户id
     * @param stateIdArray 账号的状态类型，数字表示
     * @param nowDate 现在的时间
     * @return 返回一个违规记录列表
     */
    List<Punishment> queryPunishmentList(@Param("userId") String userId,@Param("stateIdArray") Integer[] stateIdArray,@Param("nowDate") Date nowDate);

    /**
     * 查询指定账号所有生效中的违规记录
     * @param userId 用户id
     * @param nowDate 现在的时间
     * @return 返回一个违规记录列表
     */
    List<Punishment> queryPunishmentAllList(@Param("userId") String userId,@Param("nowDate") Date nowDate);
}
