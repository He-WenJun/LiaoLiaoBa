package com.hwj.tieba.dao;

import com.hwj.tieba.entity.LoginLocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface LoginLocationMapper {
    /**
     * 插入登录记录
     * @param loginIP 登录实例
     * @return 返回受影响的行数
     */
    int insertLoginIp(LoginLocation loginIP);

    /**
     * 查询指定用户ID的登录记录
     * @param userID 用户ID
     * @param startLoginDate 登录时间区间 起始登录时间
     * @param endLoginDate 登陆时间区间 结束登录时间
     * @return
     */
    List<LoginLocation> queryLoginIpList (@Param("userID") String userID, @Param("startLoginDate") Date startLoginDate, @Param("endLoginDate") Date endLoginDate);
}
