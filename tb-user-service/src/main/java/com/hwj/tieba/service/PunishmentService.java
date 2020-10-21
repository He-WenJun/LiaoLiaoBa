package com.hwj.tieba.service;

import com.hwj.tieba.entity.Punishment;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

public interface PunishmentService {
    /**
     * 查询指定账号的违规记录
     * @param userId 用户id
     * @param stateIdArray 账号的状态类型，数字表示
     * @param nowDate 现在的时间
     * @return 返回一个违规记录列表
     */
    List<Punishment> queryPunishmentList(String userId, Integer[] stateIdArray, Date nowDate);

    /**
     * 查询指定账号所有生效中的违规记录
     * @param userId 用户id
     * @param nowDate 现在的时间
     * @return 返回一个违规记录列表
     */
    List<Punishment> queryPunishmentAllList(String userId,Date nowDate);
}
