package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.OthersSetting;
import com.hwj.tieba.entity.PrivateMessage;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.feign.UserService;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.PrivateMessageService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.AccountVo;
import com.hwj.tieba.vo.PrivateMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;

    @Override
    public ServerResponse<String> addUserGoToPrivateMessageList(String targetUserId, String sessionId) {
        Account account = getAccount(sessionId);
        if(targetUserId.equals(account.getUserId())){
            return ServerResponse.createByErrorCodeMessage(1,"不能和自己私信");
        }

        //判断当前登录的账号在redis的私信Id列表中是否包含此目标账号Id
        String key = Constants.POST_TOKEN_PREFIX + "PRIVATE_MESSAGE:USER_ID:" + account.getUserId();
        List<String>  targetAccountIdList = redisUtil.getRedisTemplate().opsForList().range(key,0, -1);
        if(targetAccountIdList.size() > 0) {
            for (String tarAccountId : targetAccountIdList) {
                if (tarAccountId.equals(targetUserId)) {
                    return ServerResponse.createByErrorCodeMessage(2, "已经添加了此目标账号Id");
                }
            }
        }
        //将目标账号id添加到当前登录账号的redis私信list列表中
        redisUtil.getRedisTemplate().opsForList().rightPush(key, targetUserId);
        return ServerResponse.createBySuccessMessage("成功添加到私信列表");
    }

    @Override
    public ServerResponse<List<AccountVo>> getPrivateMessageList(String sessionId) {
        Account account = getAccount(sessionId);
        String key = Constants.POST_TOKEN_PREFIX + "PRIVATE_MESSAGE:USER_ID:" + account.getUserId();
        //取出所有私信账号的Id
        List<String> userIdList = redisUtil.getRedisTemplate().opsForList().range(key, 0, -1);

         if(userIdList.size() == 0){
            return ServerResponse.createByErrorMessage("没有可以私信的账号，快来找人聊聊吧！");
        }
        //调用user-service服务获取账号实例
        ServerResponse resultServerResponse = userService.getUserInfoList(userIdList);
        return resultServerResponse;
    }

    @Override
    public ServerResponse<PrivateMessageVo> getOldMessage(Account targetAccount, long startIndex, String sessionId) {
        Account account = getAccount(sessionId);
        String maybeKey1 = Constants.POST_TOKEN_PREFIX + "MESSAGE_RECORD:"+account.getUserId()+"AND"+targetAccount.getUserId();
        String maybeKey2 = Constants.POST_TOKEN_PREFIX + "MESSAGE_RECORD:"+targetAccount.getUserId()+"AND"+account.getUserId();

        List<PrivateMessage> privateMessageList = null;
        boolean isLastMessage = false;
        if(startIndex != -1l){
            if(startIndex - Constants.pageCountSize > 0l){
                isLastMessage = false;
            }else {
                isLastMessage = true;
            }
        }

        long endIndex = startIndex;

        if(redisUtil.hasKey(maybeKey1)){
            //每次获取10条历史记录
            if(startIndex >= 10l){
                startIndex -= Constants.pageCountSize;
            }else {
                if(startIndex == -1l){
                    endIndex = redisUtil.getRedisTemplate().opsForList().size(maybeKey1);
                    if(endIndex >= 10l){
                        startIndex = endIndex - Constants.pageCountSize;
                    }else {
                        startIndex = 0l;
                        isLastMessage = true;
                    }
                }else {
                    startIndex = 0l;
                    isLastMessage = true;
                }
            }
            privateMessageList = redisUtil.listRange(maybeKey1,startIndex, endIndex, PrivateMessage.class);
        }else if(redisUtil.hasKey(maybeKey2)){
            //每次获取10条历史记录
            if(startIndex >= 10l){
                startIndex -= Constants.pageCountSize;
            }else {
                if(startIndex == -1l){
                    endIndex = redisUtil.getRedisTemplate().opsForList().size(maybeKey2);
                    if(endIndex >= 10l){
                        startIndex = endIndex - Constants.pageCountSize;
                    }else {
                        startIndex = 0l;
                        isLastMessage = true;
                    }
                }else {
                    isLastMessage = true;
                    startIndex = 0l;
                }
            }
            privateMessageList = redisUtil.listRange(maybeKey2,startIndex, endIndex, PrivateMessage.class);
        }
        if(privateMessageList == null || privateMessageList.size() == 0){
            return ServerResponse.createByErrorMessage("没有历史记录");
        }
        startIndex --;
        return ServerResponse.createBySuccess(new PrivateMessageVo(privateMessageList, startIndex ,isLastMessage));
    }

    @Override
    public ServerResponse<String> verifyMessage(String targetUserId, String userId) {
        //判断要接收消息的用户是否拒收了发送方的消息
        String settingKey = Constants.ACCOUNT_TOKEN_PREFIX + "OTHERS_SETTING:USER_ID_" + targetUserId + ":USER_ID" + userId;
        if(redisUtil.hasKey(settingKey)){
            OthersSetting othersSetting = redisUtil.get(settingKey, OthersSetting.class);
            if(othersSetting.isSendMessage()){
                return ServerResponse.createByErrorCodeMessage(1,"对方拒收了你的消息");
            }
        }

        //判断发送消息的用户Id是否在接收方的私信列表里，若不存在，则添加
        String key = Constants.POST_TOKEN_PREFIX + "PRIVATE_MESSAGE:USER_ID:" + targetUserId;
        List<String>  privateMessageList = redisUtil.getRedisTemplate().opsForList().range(key,0, -1);
        if(privateMessageList.size() > 0) {
            for (String tarUserId : privateMessageList) {
                if (tarUserId.equals(userId)) {
                    return ServerResponse.createByErrorCodeMessage(2, "已经添加发送用户的Id");
                }
            }
        }

        redisUtil.getRedisTemplate().opsForList().rightPush(key, userId);
        return ServerResponse.createBySuccessMessage("用户Id添加成功");
    }

    @Override
    public ServerResponse<String> delPrivateMessage(String targetUserId, String sessionId) {
        Account account = getAccount(sessionId);
        String key = Constants.POST_TOKEN_PREFIX + "PRIVATE_MESSAGE:USER_ID:" + account.getUserId();
        redisUtil.getRedisTemplate().opsForList().remove(key,0,targetUserId);
        return ServerResponse.createBySuccessMessage("成功删除私信");
    }

    @Override
    public ServerResponse<String> lastMessage(String sessionId) {
        Account account = getAccount(sessionId);
        String userId = redisUtil.getRedisTemplate().opsForValue().get(Constants.POST_TOKEN_PREFIX+"LAST_MESSAGE_USER_ID:"+account.getUserId()).toString();
        if(userId == null){
            return ServerResponse.createByErrorMessage("没有上一次聊天对象");
        }
        return ServerResponse.createBySuccess(userId);
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
