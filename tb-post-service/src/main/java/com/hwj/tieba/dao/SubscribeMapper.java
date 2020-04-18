package com.hwj.tieba.dao;

import com.hwj.tieba.entity.Subscribe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SubscribeMapper {
    /**
     * 根据用户Id和订阅类型查询用户的订阅信息
     * @param userId 用户Id
     * @param typeId 类型Id
     * @return 订阅列表
     */
    List<Subscribe> quitSubscribeByUserIdAndTypeId(@Param("userId") String userId,@Param("typeId") Integer typeId);
}
