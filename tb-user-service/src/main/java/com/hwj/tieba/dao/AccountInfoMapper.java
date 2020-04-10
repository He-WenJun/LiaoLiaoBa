package com.hwj.tieba.dao;

import com.hwj.tieba.entity.AccountInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface AccountInfoMapper {
    /**
     * 插入一条账号详细信息
     * @param accountInfo 信息实例
     * @return 受影响的行数
     */
    Integer insertAccountInfo(AccountInfo accountInfo);

    /**
     * 查询多个账号的经验值和头像
     * @param userIdList 用户ID集合
     * @return 用户经验值
     */
    List<AccountInfo> quitAccountExpAndHeadPictureList(@Param("userIdList") List<String> userIdList);

    /**
     * 查询多个账号的经验值和头像
     * @param userId 用户ID
     * @return 用户头像和经验值
     */
    AccountInfo quitAccountExpAndHeadPicture(@Param("userId") String userId);

    /**
     * 修改账号经验值
     * @param exp 经验值
     * @param nowDate 修改时间
     * @return 受影响行数
     */
    Integer updateUserExp(@Param("userExp") Long exp, @Param("nowDate") Date nowDate);
}