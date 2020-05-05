package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.BaMapper;
import com.hwj.tieba.dao.SubscribeMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Ba;
import com.hwj.tieba.entity.Subscribe;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.feign.UserService;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.BaService;
import com.hwj.tieba.util.DateUtil;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.util.UUIDUtil;
import com.hwj.tieba.vo.BaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BaServiceImpl implements BaService {
    @Autowired
    private BaMapper baMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private SubscribeMapper subscribeMapper;
    @Autowired
    private UserService userService;

    @Override
    public ServerResponse<String> baSignIn(String sessionId) {
        Account account = getAccount(sessionId);
        //获取当前用户订阅的贴吧
        String subscribeKey = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId();
        List<BaVO> baVOList = null;
        List<Subscribe> subscribeList = null;
        if(redisUtil.hasKey(subscribeKey)){
            baVOList = redisUtil.getArray(subscribeKey,BaVO.class);
        }else {
            subscribeList = subscribeMapper.querySubscribeByUserIdAndTypeId(account.getUserId(),Constants.SubscribeType.BA);
        }

        if(baVOList == null && subscribeList.size() == 0){
            throw new TieBaException("您还没有订阅贴吧哦");
        }

        //取出订阅贴吧的Id
        List<String> baIdList = new ArrayList<String>();
        if(baVOList != null){
            for (BaVO baVO : baVOList){
                baIdList.add(baVO.getBaId());
            }
        }else {
            for (Subscribe subscribe : subscribeList){
                baIdList.add(subscribe.getObjectId());
            }
        }

        //组装签到key
        String signInKey = Constants.POST_TOKEN_PREFIX+"SIGN_IN:"+account.getUserId();
        //签到过的贴吧Id
        List<String> signInBaIdList = null;
        //若存在签到过的贴吧Id，则对签到过的贴吧进行过滤，避免重复签到
        if(redisUtil.hasKey(signInKey)){
            signInBaIdList = redisUtil.getArray(signInKey,String.class);
            for(int i = 0; i < baIdList.size(); i++){
                for(int j = 0; j < signInBaIdList.size(); j++){
                    if(baIdList.get(i).equals(signInBaIdList.get(j))){
                        baIdList.remove(i);
                        i--;
                        break;
                    }
                }
            }
        }

        //若要签到的贴吧Id集合长度为0则说明今天已经全部签到过了
        if(baIdList.size() == 0){
            throw new TieBaException("您今天已经全部签到过了哦");
        }

        //更新贴吧经验值
        baMapper.updateBaExpByBaId(baIdList,Constants.SING_IN_EXP);

        //将签到过的贴吧Id合并
        if(signInBaIdList == null){
            signInBaIdList = baIdList;
        }else {
            for(int i =0; i<baIdList.size(); i++){
                signInBaIdList.add(baIdList.get(i));
            }
        }

        //获取今天还剩多少秒
        int seconds = DateUtil.getSeconds();
        //将签到过的贴吧Id存缓存，用于判断是否重复签到
        redisUtil.set(signInKey,seconds,JSON.toJSON(signInBaIdList));

        //拼接用于增加账号经验值的token Key，帮助判断增加账号经验值的请求是不是系统发出的
        String increaseAccountExpKey = Constants.POST_TOKEN_PREFIX+"INCREASE_ACCOUNT_EXP_TOKEN:"+account.getUserId();
        String token = UUIDUtil.getStringUUID();
        redisUtil.setStr(increaseAccountExpKey,60,token);
        //调用user-service服务，为当前账号增加经验值
        ServerResponse serverResponse = userService.increaseAccountExp(baIdList.size() * 3,token,account.getUserId());
        System.out.println(JSON.toJSONString(serverResponse));
        serverResponse.setMsg(serverResponse.getMsg()+",成功帮助"+baIdList.size()+"个贴吧增加等级经验值");
        return serverResponse;
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
