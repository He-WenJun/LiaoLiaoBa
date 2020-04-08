package com.hwj.tieba.dao;

import com.hwj.tieba.entity.Account;
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
     * @return  返回一个账号实例
     */
    Account queryAccountByAccountAndPassword(@Param("accountNumber") String accountNumber,@Param("md5Password") String md5Password);

    /**
     * 按账号信息查询账号
     * @param account 查询的账号
     * @return 返回账号实例
     */
    List<Account> queryAccountByInfo(Account account);



    /**
     * 插入账号
     * @param account 插入的账号
     * @return
     */
    Integer insertAccount(Account account);
}
