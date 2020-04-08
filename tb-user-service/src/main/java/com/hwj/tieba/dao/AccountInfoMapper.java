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
     * @return
     */
    public Integer insertAccountInfo(AccountInfo accountInfo);

    /**
     * 查询多个账号的经验值和头像
     * @param userIdList 用户ID集合
     * @return 用户经验值
     */
    public List<AccountInfo> quitAccountExpAndHeadPictureList(@Param("userIdList") List<String> userIdList);

    /**
     * 修改账号经验值
     * @param exp 经验值
     * @param nowDate 修改时间
     * @return
     */
    public Integer updateUserExp(@Param("userExp") Long exp, @Param("nowDate") Date nowDate);
}