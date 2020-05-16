package com.hwj.tieba.dao;

import com.hwj.tieba.entity.Subscribe;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface SubscribeMapper {
    /**
     * 根据用户Id和订阅类型查询用户的订阅记录
     * @param userId 用户Id
     * @param typeId 类型Id
     * @return 订阅列表
     */
    List<Subscribe> querySubscribeByUserIdAndTypeId(@Param("userId") String userId,@Param("typeId") Integer typeId);

    /**
     * 根据用户Id和订阅目标Id查询订阅记录
     * @param objectId 目标Id
     * @param userId 用户Id
     * @return 订阅实例
     */
    Subscribe querySubscribeByObjectIdAndUserId(@Param("objectId") String objectId,@Param("userId") String userId);

    /**
     * 根据目标Id和用户Id删除订阅记录
     * @param objectId 目标Id
     * @param userId 用户Id
     * @return 受影响行数
     */
    Integer deleteSubscribeByObjectIdAndUserId(@Param("objectId") String objectId, @Param("userId") String userId);

    /**
     * 添加订阅记录
     * @param subscribe 要插入的订阅记录
     * @return 受影响行数
     */
    Integer insertSubscribe(Subscribe subscribe);

    /**
     * 查询订阅个数
     * @param objectIdList 订阅目标Id
     * @return 订阅个数
     */
    List<Map<String,Object>> countSubscribe(@Param("objectIdList") List<String> objectIdList);
}
