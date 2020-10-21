package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.OthersSetting;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.AccountSetting;
import com.hwj.tieba.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountSettingImpl implements AccountSetting {
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse<String> addOthersUserSetting(OthersSetting othersSetting, String sessionId) {
        Account account = getAccount(sessionId);
        if(othersSetting.getUserId().equals(account.getUserId())){
            throw new TieBaException("不能对自己使用此设置");
        }
        String key = Constants.TOKEN_PREFIX + "OTHERS_SETTING:USER_ID_" + account.getUserId() + ":USER_ID" + othersSetting.getUserId();
        redisUtil.set(key, 60, othersSetting);
        redisUtil.persistKey(key);
        return ServerResponse.createBySuccessMessage("已保存设置");
    }

    @Override
    public ServerResponse<OthersSetting> getOthersUserSetting(OthersSetting othersSetting, String sessionId) {
        Account account = getAccount(sessionId);
        String key = Constants.TOKEN_PREFIX + "OTHERS_SETTING:USER_ID_" + account.getUserId() + ":USER_ID" + othersSetting.getUserId();
        if(!redisUtil.hasKey(key)){
            throw new TieBaException("没有对此用户的设置");
        }
        return ServerResponse.createBySuccess(redisUtil.get(key, OthersSetting.class));
    }

    private Account getAccount(String sessionId){
        Map<String,String> sessionMap = redisUtil.hget(sessionId);
        //判断当前session当中是否存在账户号json，若不存在则未登录
        if(sessionMap.get("Account") == null){
            throw new TieBaException("未登录");
        }
        return JSON.parseObject(sessionMap.get("Account"),Account.class);
    }
}
