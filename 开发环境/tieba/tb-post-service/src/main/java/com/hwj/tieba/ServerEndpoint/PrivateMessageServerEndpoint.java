package com.hwj.tieba.ServerEndpoint;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.config.CustomSpringConfigurator;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.PrivateMessage;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.PrivateMessageService;
import com.hwj.tieba.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/privateMessage/{sessionId}", configurator = CustomSpringConfigurator.class)
public class PrivateMessageServerEndpoint {
    private Logger log = LoggerFactory.getLogger(PrivateMessageServerEndpoint.class);

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private PrivateMessageService privateMessageService;

    /**websocket 的session都存入此map*/
    private static ConcurrentHashMap<String, Session> sessionMap = new ConcurrentHashMap<>();


    /**
     * 建立连接时执行的方法
     * @param sessionId，redis储存了HTTP的session对象，key为sessionId,session中包含了当前登录的账号信息
     * @param session websocket 的session
     */
    @OnOpen
    public void onOpen(@PathParam("sessionId") String sessionId, Session session){
        Account account = getAccount(sessionId);
        System.out.println(session == null);
        //将session存入map，以当前登录的用户Id为key
        sessionMap.put(account.getUserId(), session);
    }

    /**
     * 收到消息时执行的方法
     * @param messageJsonString 消息json字符串，包含要发送的信息和要接收信息的用户Id，
     * @param session
     * @throws IOException
     */
    @OnMessage
    public void onMessage(String messageJsonString, Session session) throws IOException {
        JSONObject messageJson = JSON.parseObject(messageJsonString);
        String targetUserId = messageJson.getString("targetUserId");
        String sessionId = messageJson.getString("sessionId");
        String message = messageJson.getString("message");

        //取出当前登录的账号
        Account account = getAccount(sessionId);

        //将发送消息的用户Id添加到接收消息的用户私信列表
        ServerResponse serverResponse = privateMessageService.verifyMessage(targetUserId,account.getUserId());

        //若状态码为1，则说明接收方拒收了发送方的消息
        if(serverResponse.getStatus() == 1){
            session.getBasicRemote().sendText(JSON.toJSONString(serverResponse));
            return;
        }

        //根据接收用户的Id取出要接收信息的session
        Session targetSession = sessionMap.get(targetUserId);

        PrivateMessage privateMessage = new PrivateMessage(account.getUserId(),message);

        //若targetSession为空，则说明发送消息的目标用户没有建立连接
        if(targetSession == null){
            session.getBasicRemote().sendText(JSON.toJSONString(ServerResponse.createByErrorMessage("对方目前不在线哦")));
        }else {
            //将消息发送到目标用户
            targetSession.getBasicRemote().sendText(JSON.toJSONString(ServerResponse.createBySuccess(privateMessage)));
        }

        //将消息存入redis，作为历史记录，以发送人用户Id和要接收信息的用户Id为key
        String key = Constants.POST_TOKEN_PREFIX + "MESSAGE_RECORD:"+account.getUserId()+"AND"+targetUserId;

        //历史记录key有两种可能，两人聊天可能是你先发起的也有可能是他先发起的
        if(redisUtil.hasKey(key)){
            redisUtil.listRightPush(key, privateMessage);
        }else {
            redisUtil.listRightPush(Constants.POST_TOKEN_PREFIX + "MESSAGE_RECORD:"+targetUserId+"AND"+account.getUserId(), privateMessage);
        }

        //保存最后一次的聊天目标用户Id
        redisUtil.getRedisTemplate().opsForValue().set(Constants.POST_TOKEN_PREFIX+"LAST_MESSAGE_USER_ID:"+account.getUserId(), targetUserId);
    }

    @OnClose
    public void onClose(@PathParam("sessionId") String sessionId){
        sessionMap.remove(getAccount(sessionId).getUserId());
    }

    @OnError
    public void onError(Throwable throwable) throws IOException {
        log.error(throwable.getMessage());
        throwable.printStackTrace();
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
