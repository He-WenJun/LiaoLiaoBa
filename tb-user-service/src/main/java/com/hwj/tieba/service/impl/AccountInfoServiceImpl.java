package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.dao.AccountInfoMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.AccountInfoService;
import com.hwj.tieba.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.util.Map;

public class AccountInfoServiceImpl implements AccountInfoService {
    @Autowired
    private AccountInfoMapper accountInfoMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse getUserInfo(String sessionId) {
        //获取redis中的session对象
        Map<String,String> sessionMap = (Map<String, String>) redisUtil.hget(sessionId);
        Account account = JSON.parseObject(sessionMap.get("Account"),Account.class);
        if(StringUtils.isEmpty(account)){
            return ServerResponse.createByErrorMessage("")
        }


        return null;
    }
}
