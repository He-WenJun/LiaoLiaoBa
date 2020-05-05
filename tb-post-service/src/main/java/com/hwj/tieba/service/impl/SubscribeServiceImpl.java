package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.BaMapper;
import com.hwj.tieba.dao.FileMapper;
import com.hwj.tieba.dao.SubscribeMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Ba;
import com.hwj.tieba.entity.File;
import com.hwj.tieba.entity.Subscribe;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.SubscribeService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.util.UUIDUtil;
import com.hwj.tieba.vo.BaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SubscribeServiceImpl implements SubscribeService {
    @Autowired
    private SubscribeMapper subscribeMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private BaMapper baMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse<List<BaVO>> getSubscribeBa(String sessionId) {
        Account account = getAccount(sessionId);

        String key = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId();
        if(redisUtil.hasKey(key)){
            return ServerResponse.createBySuccess(redisUtil.getArray(key,BaVO.class));
        }

        //查询当前登录账号的订阅Id
        List<Subscribe> subscribeList = subscribeMapper.querySubscribeByUserIdAndTypeId(account.getUserId(), Constants.SubscribeType.BA);

        if(subscribeList.size() == 0){
            throw new TieBaException(2,"您还没有订阅的贴吧哦");
        }

        List<String> subscribeIdList = new ArrayList<String>(subscribeList.size());
        for(Subscribe subscribe : subscribeList){
            subscribeIdList.add(subscribe.getObjectId());
        }

        //根据订阅Id查询吧信息
        List<Ba> baList = baMapper.queryBaByIdList(subscribeIdList);

        //获取吧的头像Id
        List<String> headPictureIdList = new ArrayList<String>(baList.size());
        for (Ba ba : baList){
            headPictureIdList.add(ba.getHeadPictureId());
        }
        //根据头像Id查询图片
        List<File> headPictureList =  fileMapper.queryFileListById(headPictureIdList);

        List<BaVO> baVOList = new ArrayList<BaVO>(baList.size());

        //计算吧等级并组装信息
        for (int i = 0; i < baList.size(); i++ ){
            int level = 1;
            Long exp = baList.get(i).getExp();
            String headPictureSrc = null;

            //若当前吧的经验值大于每级经验值所需经验，则计算吧等级，并算出经验值余数
            if(baList.get(i).getExp() >= Constants.LEVEL){
                level = (int)( baList.get(i).getExp() / Constants.LEVEL);
                exp = baList.get(i).getExp() % Constants.LEVEL;
            }

            //循环取出头像路径
            for (int j = 0; j < headPictureList.size(); j++){
                //若吧的头像id与头像id相同，则获取头像路径
                if(baList.get(i).getHeadPictureId().equals(headPictureList.get(j).getId())){
                    headPictureSrc = headPictureList.get(j).getSrc();
                    break;
                }
            }

            baVOList.add(new BaVO(baList.get(i).getBaId(),null,baList.get(i).getBaName(),
                    baList.get(i).getIntroduce(),level,exp,headPictureSrc,null,baList.get(i).getEnrollDate()));
        }


        //将订阅的吧存入redis
        redisUtil.set(Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId(),Constants.KEY_EXPIRES,baVOList);

        return ServerResponse.createBySuccess(baVOList);
    }

    @Override
    public ServerResponse<String> delSubscribe(String sessionId,String objectId) {
        if(StringUtils.isEmpty(objectId)){
            throw new TieBaException("参数错误");
        }

        Account account = getAccount(sessionId);

        subscribeMapper.deleteSubscribeByObjectIdAndUserId(objectId,account.getUserId());

        String key = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId();
        //删除缓存当中的订阅缓存
        redisUtil.del(key);

        return ServerResponse.createBySuccess("成功取消订阅",null );
    }

    @Override
    public ServerResponse<String> addSubscribeBa(String sessionId, String baName) {
        if(StringUtils.isEmpty(baName)){
            throw new TieBaException("参数有误");
        }

        Account account = getAccount(sessionId);
        //验证用户是否已经订阅了这个贴吧，不能重复订阅
        String key = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId();
        Ba subscribeBa = null;
        if(redisUtil.hasKey(key)){
            List<BaVO> baVOList = redisUtil.getArray(key,BaVO.class);
            for(BaVO baVO : baVOList){
                if(baVO.getBaName().equals(baName)){
                    throw new TieBaException("你已经订阅过这个贴吧了");
                }
            }
        }else {
            subscribeBa = baMapper.queryBaById(baName);
            if(subscribeBa == null){
                throw new TieBaException("没有找到这个贴吧");
            }
            Subscribe resultSubscribe = subscribeMapper.querySubscribeByObjectIdAndUserId(subscribeBa.getBaId(),account.getUserId());
            if(resultSubscribe != null){
                throw new TieBaException("你已经订阅过这个贴吧了");
            }
        }

        if(subscribeBa == null){
            subscribeBa = baMapper.queryBaById(baName);
            if(subscribeBa == null){
                throw new TieBaException("没有找到这个贴吧");
            }
        }

        //实例化订阅对象，插入订阅表
        Date nowDate = new Date();
        Subscribe subscribe = new Subscribe(UUIDUtil.getStringUUID(),Constants.SubscribeType.BA,subscribeBa.getBaId(),account.getUserId(),nowDate,nowDate);
        subscribeMapper.insertSubscribe(subscribe);
        //删除订阅的贴吧的缓存
        redisUtil.del(key);
        return ServerResponse.createBySuccess("订阅成功",null);
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
