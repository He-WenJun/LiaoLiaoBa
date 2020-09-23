package com.hwj.tieba.dao;

import com.hwj.tieba.entity.Account;
import com.hwj.tieba.resp.ServerResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AccountMapper {
    /**
     * 按账号和密码查询账号记录
     * @param accountNumber 账号 既可以是用户名也可以是邮箱
     * @param md5Password 账号密码
     * @return
     */
    Account queryAccountByAccountAndPassword(@Param("accountNumber") String accountNumber,@Param("md5Password") String md5Password);

    /**
     * 按账号信息查询账号
     * @param account 查询的账号
     * @return
     */
    List<Account> queryAccount(Account account);

    /**
     * 按账号信息查询账号列表
     * @param accountList 查询的账号集合
     * @return
     */
    List<Account> queryAccountList(@Param("accountList") List<Account> accountList);

    /**
     * 插入账号
     * @param account 插入的账号
     * @return
     */
    Integer insertAccount(Account account);

    /**
     * 按Id更新账号
     * @param account
     * @return
     */
    Integer updateUserByUserId(Account account);

}
